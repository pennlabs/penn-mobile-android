package com.pennapps.labs.pennmobile.laundry.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // if this value is already true, then simply attach adapter
        if (laundryViewModel.loadedRooms.value != null && laundryViewModel.loadedRooms.value!!) {
            attachAdapter()
            binding.loadingPanel.root.visibility = View.GONE
            binding.noResults.root.visibility = View.GONE
        } else {
            // otherwise, wait until the network request is done
            laundryViewModel.loadedRooms.observe(viewLifecycleOwner) { loaded ->
                if (loaded) {
                    attachAdapter()
                    binding.loadingPanel.root.visibility = View.GONE
                    binding.noResults.root.visibility = View.GONE
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
                val bearerToken =
                    "Bearer " +
                        sharedPreferences
                            .getString(getString(R.string.access_token), "")
                            .toString()
                laundryViewModel.setFavoritesFromToggled(mStudentLife, bearerToken)
            }
        }
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.visibility = View.GONE
        _binding = null
    }
}
