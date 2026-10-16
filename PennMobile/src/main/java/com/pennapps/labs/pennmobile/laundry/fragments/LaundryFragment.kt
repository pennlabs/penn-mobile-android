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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentLaundryBinding
import com.pennapps.labs.pennmobile.isOnline
import com.pennapps.labs.pennmobile.laundry.LaundryMonitorViewModel
import com.pennapps.labs.pennmobile.laundry.LaundryViewModel
import com.pennapps.labs.pennmobile.laundry.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.laundry.classes.LaundryUsage
import com.pennapps.labs.pennmobile.laundry.composables.LaundryMonitorButton
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaundryFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private lateinit var mStudentLife: StudentLife
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

        mStudentLife = MainActivity.studentLifeInstance
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

            mAdapter?.notifyDataSetChanged()

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
        binding.laundrySegmentedButton.setContent {
            // hiltViewModel() rather than `by viewModels()`: fragment 1.5.1 + lifecycle 2.9
            // crash on Hilt VMs with missing CreationExtras.
            val viewModel: LaundryMonitorViewModel = hiltViewModel()
            LaundryMonitorControls(viewModel)
        }
    }
}

@Composable
private fun LaundryMonitorControls(viewModel: LaundryMonitorViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val monitorMode by viewModel.monitorMode.collectAsStateWithLifecycle()
    var pendingMonitorModeValue by rememberSaveable { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val requestedMode = pendingMonitorModeValue?.let(MonitorMode::fromPrefValue)
            pendingMonitorModeValue = null

            if (granted && requestedMode != null) {
                viewModel.onModeSelected(requestedMode)
            } else if (!granted) {
                viewModel.onModeSelected(MonitorMode.OFF)
                Toast.makeText(context, "We need notification permission to alert you", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.snackBarEvent.collect { event ->
                val message = event.message ?: return@collect
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetSnackBarEvent()
            }
        }
    }

    LaundryMonitorButton(
        selectedMode = monitorMode,
        onModeChange = { mode ->
            if (mode == MonitorMode.OFF) {
                pendingMonitorModeValue = null
                viewModel.onModeSelected(mode)
            } else if (hasNotificationPermission(context)) {
                viewModel.onModeSelected(mode)
            } else {
                pendingMonitorModeValue = mode.prefValue
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
    )
}

private fun hasNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
