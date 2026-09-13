package com.pennapps.labs.pennmobile.laundry.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentLaundrySettingsBinding
import com.pennapps.labs.pennmobile.laundry.LaundryViewModel
import com.pennapps.labs.pennmobile.laundry.adapters.LaundrySettingsAdapter

class LaundrySettingsFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    private lateinit var mContext: Context
    private lateinit var toolbar: Toolbar

    private var _binding: FragmentLaundrySettingsBinding? = null
    val binding get() = _binding!!

    private val laundryViewModel: LaundryViewModel by activityViewModels()
    private var adapterAttached = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        mContext = mActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLaundrySettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private fun attachAdapter() {
        val mAdapter = LaundrySettingsAdapter(mContext, laundryViewModel)
        try {
            binding.laundryBuildingExpandableList.setAdapter(mAdapter)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = mActivity.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE

        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity.closeKeyboard()
        mActivity.hideBottomBar()

        binding.loadingPanel.root.visibility = View.VISIBLE

        // The adapter snapshots the current favorites as its baseline on construction, so it
        // must not be attached until both the room list and the favorites have arrived.
        laundryViewModel.loadedRooms.observe(viewLifecycleOwner) { attachAdapterIfLoaded() }
        laundryViewModel.loadedFavorites.observe(viewLifecycleOwner) { attachAdapterIfLoaded() }

        laundryViewModel.getHalls(mStudentLife)

        // Normally the Laundry page has already fetched these; do it here too so the screen
        // still resolves if it never got the chance (e.g. it was offline).
        if (laundryViewModel.loadedFavorites.value != true) {
            mActivity.mNetworkManager.getAccessToken {
                val bearerToken =
                    "Bearer " +
                        sharedPreferences
                            .getString(mActivity.getString(R.string.access_token), "")
                            .toString()
                laundryViewModel.getFavorites(mStudentLife, bearerToken)
            }
        }
    }

    private fun attachAdapterIfLoaded() {
        if (_binding == null || adapterAttached) return
        if (laundryViewModel.loadedRooms.value != true) return
        if (laundryViewModel.loadedFavorites.value != true) return

        adapterAttached = true
        attachAdapter()
        binding.loadingPanel.root.visibility = View.GONE
        binding.noResults.root.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Route through the activity so Up and system Back tear the fragment down
            // identically -- both then run onDestroyView(), which is where the save lives.
            mActivity.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.laundry)
        mActivity.setSelectedTab(MainActivity.LAUNDRY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (laundryViewModel.existsDiff()) {
            // getAccessToken always defers its callback onto the activity's lifecycleScope,
            // so it cannot run before this fragment is detached. Everything the callback
            // touches is therefore activity-scoped (the view model included) rather than
            // fragment-scoped -- an isAdded check here would drop the save every time.
            val activity = mActivity
            val studentLife = mStudentLife
            val prefs = sharedPreferences
            val viewModel = laundryViewModel
            val tokenKey = activity.getString(R.string.access_token)

            activity.mNetworkManager.getAccessToken {
                val bearerToken = "Bearer " + prefs.getString(tokenKey, "").toString()
                viewModel.setFavoritesFromToggled(studentLife, bearerToken)
            }
        }
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.visibility = View.GONE
        adapterAttached = false
        _binding = null
    }
}
