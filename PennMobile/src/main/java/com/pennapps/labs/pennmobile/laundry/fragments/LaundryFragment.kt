package com.pennapps.labs.pennmobile.laundry.fragments

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentLaundryBinding
import com.pennapps.labs.pennmobile.isOnline
import com.pennapps.labs.pennmobile.laundry.LaundryAvailabilityWorker
import com.pennapps.labs.pennmobile.laundry.LaundryViewModel
import com.pennapps.labs.pennmobile.laundry.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.laundry.classes.LaundryUsage
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LaundryFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private lateinit var mStudentLife: StudentLife
    private lateinit var mContext: Context

    private lateinit var sharedPreferences: SharedPreferences

    // state for monitoring mode segmented button
    // mutableStateOf to make sure button immediately updates
    // on mode changes
    private var monitoringMode by mutableStateOf("OFF")

    // list of favorite laundry rooms
    private var laundryRooms = ArrayList<LaundryRoom>()

    // data for laundry room usage
    private var roomsData: ArrayList<LaundryUsage> = ArrayList()

    private var mAdapter: LaundryRoomAdapter? = null

    private var _binding: FragmentLaundryBinding? = null
    val binding get() = _binding!!

    private val laundryViewModel: LaundryViewModel by activityViewModels()

    // Runtime notification permission launcher
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(mContext, "We need notification permission to alert you", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mContext = mActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        monitoringMode = sharedPreferences.getString("laundry_monitor_mode", "OFF") ?: "OFF"
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

    override fun onResume() {
        super.onResume()
        // Resync
        monitoringMode = sharedPreferences.getString("laundry_monitor_mode", "OFF") ?: "OFF"
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

        binding.favoriteLaundryList.setRecycledViewPool(
            RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(0, LaundryViewModel.MAX_NUM_ROOMS)
            },
        )
        binding.favoriteLaundryList.itemAnimator = null

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

            mAdapter?.let {
                for (pos in 0 until LaundryViewModel.MAX_NUM_ROOMS) {
                    it.notifyItemChanged(pos)
                }
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
        // remember(monitoringMode) updates selectedIndex when monitoringMode changes
        binding.laundrySegmentedButton.setContent {
            var selectedIndex by remember(monitoringMode) {
                mutableIntStateOf(
                    when (monitoringMode) {
                        "WASHERS" -> 1
                        "DRYERS" -> 2
                        else -> 0
                    },
                )
            }
            val options = listOf("OFF", "WASHERS", "DRYERS")
            val totalItems = options.size + 1

            SingleChoiceSegmentedButtonRow {
                // bell icon label, signals this row controls notifications
                SegmentedButton(
                    modifier = Modifier.weight(0.5f),
                    selected = false,
                    onClick = {},
                    enabled = false,
                    shape = SegmentedButtonDefaults.itemShape(0, totalItems),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                    )
                }

                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        modifier = Modifier.weight(1f),
                        selected = index == selectedIndex,
                        onClick = {
                            val newMode =
                                when (index) {
                                    1 -> "WASHERS"
                                    2 -> "DRYERS"
                                    else -> "OFF"
                                }
                            selectedIndex = index
                            monitoringMode = newMode
                            onMonitoringModeChanged(newMode)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index + 1, totalItems),
                    ) {
                        Text(label)
                    }
                }
            }
        }
    }

    private fun onMonitoringModeChanged(mode: String) {
        sharedPreferences.edit().putString("laundry_monitor_mode", mode).apply()
        val workManager = WorkManager.getInstance(mContext)
        if (mode == "OFF") {
            workManager.cancelUniqueWork("laundry_availability_monitor")
            return
        }

        // Request POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // check if any already available before polling
        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                "Bearer " +
                    sharedPreferences
                        .getString(getString(R.string.access_token), "")
                        .toString()
            viewLifecycleOwner.lifecycleScope.launch {
                val isAvailable = checkImmediateAvailability(bearerToken, mode)
                if (isAvailable) { // notify with message
                    Toast
                        .makeText(
                            mContext,
                            "A ${mode.lowercase()} is already available!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    sharedPreferences.edit().putString("laundry_monitor_mode", "OFF").apply()
                    monitoringMode = "OFF"
                } else {
                    // start polling
                    val inputData =
                        Data
                            .Builder()
                            .putString("monitor_mode", mode)
                            .putLong("start_time", System.currentTimeMillis())
                            .build()

                    val workRequest =
                        OneTimeWorkRequest
                            .Builder(LaundryAvailabilityWorker::class.java)
                            .setInitialDelay(3, TimeUnit.MINUTES)
                            .setInputData(inputData)
                            .build()

                    workManager.enqueueUniqueWork(
                        "laundry_availability_monitor",
                        ExistingWorkPolicy.REPLACE,
                        workRequest,
                    )

                    Toast
                        .makeText(
                            mContext,
                            "Monitoring ${mode.lowercase()} for 1 hour",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    // call api and check for immediate avail
    private suspend fun checkImmediateAvailability(
        bearerToken: String,
        mode: String,
    ): Boolean {
        val favoriteIds =
            try {
                val response = mStudentLife.getLaundryPref(bearerToken) // get preferences from api
                if (response.isSuccessful) response.body()?.rooms ?: emptyList() else emptyList()
            } catch (e: Exception) {
                emptyList()
            }

        for (roomId in favoriteIds) {
            try {
                val response = mStudentLife.room(roomId)
                if (response.isSuccessful) {
                    val room = response.body() ?: continue
                    // use the server computed count
                    val machineList =
                        if (mode == "WASHERS") room.machines?.washers else room.machines?.dryers
                    if ((machineList?.open ?: 0) > 0) return true
                }
            } catch (e: Exception) {
                continue
            }
        }
        return false
    }
}
