package com.pennapps.labs.pennmobile

import StudentLifeRf2
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.adapters.LaundrySettingsAdapter
import com.pennapps.labs.pennmobile.databinding.FragmentLaundrySettingsBinding
import com.pennapps.labs.pennmobile.viewmodels.LaundryViewModel
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*

class LaundrySettingsFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLifeRf2
    private lateinit var mContext: Context

    private var _binding : FragmentLaundrySettingsBinding? = null
    private val binding get() = _binding!!

    private val laundryViewModel : LaundryViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstanceRf2
        mActivity = activity as MainActivity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        mContext = mActivity
        mActivity.closeKeyboard()
        mActivity.toolbar.visibility = View.VISIBLE
        mActivity.hideBottomBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaundrySettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        // set up back button
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingPanel?.visibility = View.VISIBLE

        // if this value is already true, then simply attach adapter
        if (laundryViewModel.loadedRooms.value != null && laundryViewModel.loadedRooms.value!!) {
            attachAdapter()
            loadingPanel?.visibility = View.GONE
            no_results?.visibility = View.GONE
        } else {
            // otherwise, wait until the network request is done
            laundryViewModel.loadedRooms.observe(viewLifecycleOwner) { loaded ->
                if (loaded) {
                    attachAdapter()
                    loadingPanel?.visibility = View.GONE
                    no_results?.visibility = View.GONE
                }
            }
            laundryViewModel.getHalls(mStudentLife)
        }
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
            mActivity.mNetworkManager.getAccessToken {
                val bearerToken = "Bearer " + sharedPreferences
                    .getString(getString(R.string.access_token), "").toString()
                laundryViewModel.setFavoritesFromToggled(mStudentLife, bearerToken)
            }
        }
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity.toolbar.visibility = View.GONE
        _binding = null
    }
}