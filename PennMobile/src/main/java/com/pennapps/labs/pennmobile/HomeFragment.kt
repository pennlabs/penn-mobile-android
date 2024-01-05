package com.pennapps.labs.pennmobile

import android.content.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter2
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.HomepageViewModel
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentHomeBinding
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homepageViewModel : HomepageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        LocalBroadcastManager
            .getInstance(mActivity)
            .registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "11")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        view.home_cells_rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout
            .setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout
            .setOnRefreshListener {
                refreshHomePage()
            }

        initAppBar(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homepageViewModel.resetBlurViews()
        getHomePage()
        homepageViewModel.blurViewsLoaded.observe(viewLifecycleOwner) { loaded ->
            if (loaded) {
                binding.homeCellsRv.visibility = View.VISIBLE
                loadingPanel?.visibility = View.GONE
            }
        }
    }

    private fun getOnline() : Boolean {
        //displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionHome.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessage.text = getString(R.string.internet_error)
            binding.homeCellsRv.setPadding(0, 90, 0, 0)
            binding.internetConnectionHome.visibility = View.VISIBLE
            binding.homeRefreshLayout.isRefreshing = false
            loadingPanel?.visibility = View.GONE
            return false
        }

        binding.internetConnectionHome.visibility = View.GONE
        binding.homeCellsRv.setPadding(0, 0, 0, 0)
        return true
    }
    private fun refreshHomePage() {
        mActivity.showBottomBar()


        if (!getOnline()) {
            return
        }

        val studentLife = MainActivity.studentLifeInstance
        mActivity.mNetworkManager.getAccessToken {
            val sp = sharedPreferences
            val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
            val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()

            lifecycleScope.launch(Dispatchers.Default) {
                if (binding.homeCellsRv.adapter == null) {
                    homepageViewModel.populateHomePageCells(studentLife, bearerToken, deviceID) {
                        mActivity.runOnUiThread {
                            binding.homeCellsRv.adapter = HomeAdapter2(homepageViewModel)
                            binding.homeCellsRv.visibility = View.INVISIBLE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }
                } else {
                    homepageViewModel.updateHomePageCells(studentLife, bearerToken, deviceID, { pos ->
                       mActivity.runOnUiThread {
                           binding.homeCellsRv.adapter!!.notifyItemChanged(pos)
                       }
                    }, {
                       mActivity.runOnUiThread {
                           binding.homeRefreshLayout.isRefreshing = false
                       }
                    })
                }
            }
        }
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
            val bearerToken =
                "Bearer " + sp.getString(getString(R.string.access_token), "").toString()

            lifecycleScope.launch(Dispatchers.Default) {
                homepageViewModel.populateHomePageCells(studentLife, bearerToken, deviceID) {
                    mActivity.runOnUiThread {
                        binding.homeCellsRv.adapter = HomeAdapter2(homepageViewModel)
                        binding.homeCellsRv.visibility = View.INVISIBLE
                        binding.internetConnectionHome.visibility = View.GONE
                        binding.homeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        this.setTitle(getString(R.string.home))
        mActivity.toolbar.visibility = View.GONE
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (!initials.isNullOrEmpty()) {
            binding.initials.text = initials
        } else {
            binding.profileBackground.setImageDrawable(
                resources.getDrawable
                (R.drawable.ic_guest_avatar, context?.theme))
        }
        mActivity.setSelectedTab(MainActivity.HOME)
        mActivity.showBottomBar()
    }

    private fun setTitle(title: CharSequence) {
        binding.titleView.text = title
    }

    private fun initAppBar(view: View) {
        val firstName = sharedPreferences.getString(getString(R.string.first_name), null)
        firstName?.let {
            view.date_view.text = "Welcome, $it!".toUpperCase(Locale.getDefault())
            Handler().postDelayed(
                {
                    view.date_view.text = Utils.getCurrentSystemTime()
                },
                4000
            )
        } ?: run {
            view.date_view.text = Utils.getCurrentSystemTime()
        }
        (view.appbar_home.layoutParams
            as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        view.profile.setOnClickListener {
            //TODO: Account Settings
        }
    }
}
