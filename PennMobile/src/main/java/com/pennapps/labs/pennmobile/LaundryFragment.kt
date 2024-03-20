package com.pennapps.labs.pennmobile

import StudentLife
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentLaundryBinding
import com.pennapps.labs.pennmobile.utils.Utils
import com.pennapps.labs.pennmobile.viewmodels.LaundryViewModel
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.loading_panel.view.*
import kotlinx.android.synthetic.main.no_results.*
import java.util.*

class LaundryFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    private lateinit var mStudentLife: StudentLife
    private lateinit var mContext: Context

    private var sp: SharedPreferences? = null

    // list of favorite laundry rooms
    private var laundryRooms = ArrayList<LaundryRoom>()
    // data for laundry room usage
    private var roomsData: ArrayList<LaundryUsage> = ArrayList()
    private val maxRooms = 3

    private var mAdapter: LaundryRoomAdapter? = null

    private var numRooms: Int = 0

    private var _binding : FragmentLaundryBinding? = null
    private val binding get() = _binding!!

    private val laundryViewModel : LaundryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mContext = mActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaundryBinding.inflate(inflater, container, false)
        val view = binding.root

        initAppBar()

        binding.favoriteLaundryList.layoutManager = LinearLayoutManager(mContext)
        binding.laundryMachineRefresh.setOnRefreshListener {
            laundryViewModel.getFavorites(mStudentLife, "Bearer eEc79lTYqHBuUHssUzTYXiImyMG6U9")
        }
        binding.laundryMachineRefresh.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.removeTabs()
        numRooms = sp?.getInt(mContext.getString(R.string.num_rooms_pref), 100) ?: 0

        mActivity.setTitle(R.string.laundry)
        loadingPanel?.visibility = View.VISIBLE

        mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
        binding.favoriteLaundryList.adapter = mAdapter

        laundryViewModel.favoriteRooms.observe(viewLifecycleOwner) { favorites ->
            binding.laundryMachineRefresh.isRefreshing = false

            laundryRooms.clear()
            roomsData.clear()

            laundryRooms.addAll(favorites.favoriteRooms)
            roomsData.addAll(favorites.roomsData)

            for (pos in 0 until maxRooms) {
                mAdapter!!.notifyItemChanged(pos)
            }

            loadingPanel?.visibility = View.GONE
            binding.laundryHelpText.visibility = View.INVISIBLE
        }

        laundryViewModel.getFavorites(mStudentLife, "Bearer eEc79lTYqHBuUHssUzTYXiImyMG6U9")
    }

    private fun initAppBar() {
        (binding.appbarHome.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        binding.titleView.text = getString(R.string.laundry)
        binding.dateView.text = Utils.getCurrentSystemTime()
        binding.laundryPreferences.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LaundrySettingsFragment())
                    .addToBackStack("Laundry Settings Fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }
}
