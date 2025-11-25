package com.pennapps.labs.pennmobile.home.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentHomeBinding
import com.pennapps.labs.pennmobile.home.HomepageViewModel
import com.pennapps.labs.pennmobile.home.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.isOnline
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    private val homepageViewModel: HomepageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        initAppBar(view)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        /* Mildly suspicious idea to hide the RecyclerView until blurviews (for news and posts) are
        done processing. Processing the blurviews is slow and makes the app looks sloppy. Ideally
        this is replaced by something less hacky (such as using a ListView instead) though perhaps
        it is simpler to just remove the blur altogether.

        This takes advantage of a RecyclerView idiosyncracy: when a RecyclerView resides inside a
        nested scrollview, all of the elements are inflated:
        https://stackoverflow.com/questions/44453846/recyclerview-inside-nestedscrollview-causes-recyclerview-to-inflate-all-elements
        https://www.reddit.com/r/androiddev/comments/d8gi9v/recyclerview_inside_nestedscrollview_causes/

        This is can be used to figure out when the blurviews are finished processing.

        Since when the adapter is set in getHomePage, onBindViewHolder() is called for each cell.
        Thus, for the news and post cells which use blur, when the blur is finished processing,
        the adapter notifies homepageViewModel. When both blurs are processed, the blurViewsLoaded
        liveData in the ViewModel is toggled to true which HomeFragment observes.

        If in the future, the homepage is stuck on loading forever, this might be why. To remove
        this functionality and  stop waiting for the blur views to finish, just remove the observer
        below and change getHomePage() so that when HomeAdapter is set, homeCellsRv.visibility is
        set to View.VISIBLE instead of View.INVISIBLE and hide loadingPanel
         */
        toolbar = mActivity.findViewById(R.id.toolbar)
        binding.homeCellsRv.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false,
            )

        binding.homeRefreshLayout
            .setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        binding.homeRefreshLayout
            .setOnRefreshListener {
                getHomePage()
            }
        homepageViewModel.resetBlurViews()
        homepageViewModel.blurViewsLoaded.observe(viewLifecycleOwner) { loaded ->
            if (loaded) {
                binding.homeCellsRv.visibility = View.VISIBLE
                binding.loadingPanel.root.visibility = View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homepageViewModel.updateState.collect { updateState ->
                    updateState.positions.firstOrNull()?.let { pos ->
                        if (binding.homeCellsRv.adapter != null) {
                            mActivity.runOnUiThread {
                                binding.homeCellsRv.adapter!!.notifyItemChanged(pos)
                            }
                            homepageViewModel.updatedPosition(pos)
                        }
                    }
                }
            }
        }

        getHomePage()
    }

    private fun getOnline(): Boolean {
        // displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionHome.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessage.text = getString(R.string.internet_error)
            binding.homeCellsRv.setPadding(0, 90, 0, 0)
            binding.internetConnectionHome.visibility = View.VISIBLE
            binding.homeRefreshLayout.isRefreshing = false
            binding.loadingPanel.root.visibility = View.GONE
            return false
        }

        binding.internetConnectionHome.visibility = View.GONE
        binding.homeCellsRv.setPadding(0, 0, 0, 0)
        return true
    }

    private fun getHomePage() {
        mActivity.showBottomBar()

        if (!getOnline()) {
            return
        }

        val studentLife = MainActivity.studentLifeInstance
        mActivity.mNetworkManager.getAccessToken {
            val sp = sharedPreferences
            val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
            val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()

            val isLoggedIn = !sp.getBoolean(mActivity.getString(R.string.guest_mode), false)

            lifecycleScope.launch(Dispatchers.Default) {
                // set adapter if it is null
                if (binding.homeCellsRv.adapter == null) {
                    homepageViewModel.populateHomePageCells(
                        studentLife,
                        isLoggedIn,
                        bearerToken,
                        deviceID,
                    )
                    withContext(Dispatchers.Main) {
                        binding.homeCellsRv.adapter = HomeAdapter(homepageViewModel)
                        binding.homeCellsRv.visibility = View.INVISIBLE
                        binding.internetConnectionHome.visibility = View.GONE
                        binding.homeRefreshLayout.isRefreshing = false
                    }
                } else { // otherwise, call updateHomePageCells which only updates the cells that are changed
                    val updatedIndices =
                        homepageViewModel.updateHomePageCells(
                            studentLife,
                            isLoggedIn,
                            bearerToken,
                            deviceID,
                        )
                    withContext(Dispatchers.Main) {
                        updatedIndices.forEach { pos ->
                            binding.homeCellsRv.adapter!!.notifyItemChanged(pos)
                        }
                        binding.homeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getOnline()
        mActivity.removeTabs()
        this.setTitle(getString(R.string.home))
        toolbar.visibility = View.GONE
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (!initials.isNullOrEmpty()) {
            binding.initials.text = initials
        } else {
            binding.profileBackground.setImageDrawable(
                resources.getDrawable
                    (R.drawable.ic_guest_avatar, context?.theme),
            )
        }

        // Force update the bottom nav selection
        mActivity.setSelectedTab(MainActivity.HOME)

        // Alternative: If setSelectedTab isn't working, directly access the bottom nav
        // mActivity.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.navigation_home

        mActivity.showBottomBar()
    }

    private fun setTitle(title: CharSequence) {
        binding.titleView.text = title
    }

    private fun initAppBar(view: View) {
        val firstName = sharedPreferences.getString(getString(R.string.first_name), null)
        firstName?.let {
            binding.dateView.text = "Welcome, $it!".uppercase(Locale.getDefault())
            Handler().postDelayed(
                {
                    // Check if binding is still valid before accessing it
                    _binding?.let { binding ->
                        binding.dateView.text = Utils.getCurrentSystemTime()
                    }
                },
                4000,
            )
        } ?: run {
            binding.dateView.text = Utils.getCurrentSystemTime()
        }
        (
                binding.appbarHome.layoutParams
                        as CoordinatorLayout.LayoutParams
                ).behavior = ToolbarBehavior()
        binding.profile.setOnClickListener {
            // TODO: Account Settings
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
