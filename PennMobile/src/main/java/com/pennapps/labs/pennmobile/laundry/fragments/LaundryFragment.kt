package com.pennapps.labs.pennmobile.laundry.fragments

import StudentLifeRf2
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentLaundryBinding
import com.pennapps.labs.pennmobile.isOnline
import com.pennapps.labs.pennmobile.laundry.LaundryViewModel
import com.pennapps.labs.pennmobile.laundry.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.laundry.classes.LaundryUsage
import com.pennapps.labs.pennmobile.utils.Utils

class LaundryFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private lateinit var mStudentLife: StudentLifeRf2
    private lateinit var mContext: Context

    private lateinit var sharedPreferences: SharedPreferences

    // list of favorite laundry rooms
    private var laundryRooms = ArrayList<LaundryRoom>()

    // data for laundry room usage
    private var roomsData: ArrayList<LaundryUsage> = ArrayList()

    private var mAdapter: LaundryRoomAdapter? = null

    private var _binding: FragmentLaundryBinding? = null
    val binding get() = _binding!!

    private val laundryViewModel: LaundryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStudentLife = MainActivity.studentLifeInstanceRf2
        mActivity = activity as MainActivity
        mContext = mActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLaundryBinding.inflate(inflater, container, false)
        val view = binding.root

        initAppBar()

        binding.favoriteLaundryList.layoutManager = LinearLayoutManager(mContext)
        binding.laundryMachineRefresh.setOnRefreshListener {
            updateMachines()
        }
        binding.laundryMachineRefresh.setColorSchemeResources(
            R.color.color_accent,
            R.color.color_primary,
        )

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.removeTabs()
        mActivity.setTitle(R.string.laundry)

        mAdapter =
            LaundryRoomAdapter(
                mContext,
                laundryRooms,
                roomsData,
                false,
            )
        binding.favoriteLaundryList.adapter = mAdapter

        binding.loadingPanel.root.visibility = View.VISIBLE

        laundryViewModel.favoriteRooms.observe(viewLifecycleOwner) { favorites ->
            binding.laundryMachineRefresh.isRefreshing = false

            laundryRooms.clear()
            roomsData.clear()

            laundryRooms.addAll(favorites.favoriteRooms)
            roomsData.addAll(favorites.roomsData)

            // sort laundry rooms data by hall name
            roomsData.sortWith { usage1, usage2 -> usage2.id - usage1.id }
            laundryRooms.sortWith { room1, room2 -> room2.id - room1.id }

            for (pos in 0 until LaundryViewModel.MAX_NUM_ROOMS) {
                mAdapter!!.notifyItemChanged(pos)
            }

            binding.loadingPanel.root.visibility = View.GONE
            binding.laundryHelpText.visibility = View.INVISIBLE
        }

        updateMachines()
    }

    private fun getOnline(): Boolean {
        // displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionLaundry.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageLaundry.text = getString(R.string.internet_error)
            binding.internetConnectionLaundry.visibility = View.VISIBLE
            binding.laundryHelpText.visibility = View.INVISIBLE
            binding.laundryMachineRefresh.isRefreshing = false
            binding.loadingPanel.root.visibility = View.GONE
            return false
        }

        binding.internetConnectionLaundry.visibility = View.GONE
        return true
    }

    private fun updateMachines() {
        if (!getOnline()) {
            return
        }
        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                "Bearer " +
                    sharedPreferences
                        .getString(getString(R.string.access_token), "")
                        .toString()
            laundryViewModel.getFavorites(mStudentLife, bearerToken)
        }
    }

    private fun initAppBar() {
        (binding.appbarHome.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        binding.titleView.text = getString(R.string.laundry)
        binding.dateView.text = Utils.getCurrentSystemTime()
        binding.laundryPreferences.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, LaundrySettingsFragment())
                .addToBackStack("Laundry Settings Fragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}
