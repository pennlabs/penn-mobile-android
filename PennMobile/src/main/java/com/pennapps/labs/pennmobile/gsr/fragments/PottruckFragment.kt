package com.pennapps.labs.pennmobile.gsr.fragments

import StudentLifeRf2
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentPottruckBinding
import com.pennapps.labs.pennmobile.fitness.FitnessPreferenceViewModel
import com.pennapps.labs.pennmobile.fitness.adapters.FitnessAdapter
import com.pennapps.labs.pennmobile.fitness.adapters.FitnessHeaderAdapter
import com.pennapps.labs.pennmobile.fitness.fragments.CloseListener
import com.pennapps.labs.pennmobile.fitness.fragments.FitnessPreferencesFragment
import com.pennapps.labs.pennmobile.isOnline
import com.pennapps.labs.pennmobile.utils.Utils
import rx.schedulers.Schedulers

class PottruckFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    private lateinit var mStudentLifeRf2: StudentLifeRf2

    private lateinit var mView: View
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    private lateinit var dataModel: FitnessPreferenceViewModel
    private lateinit var favoritesAdapter: FitnessAdapter
    private lateinit var otherAdapter: FitnessAdapter
    private lateinit var favoriteHeaderAdapter: FitnessHeaderAdapter
    private lateinit var otherHeaderAdapter: FitnessHeaderAdapter

    private var _binding: FragmentPottruckBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mStudentLifeRf2 = MainActivity.studentLifeInstanceRf2
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mActivity.hideBottomBar()
        _binding = FragmentPottruckBinding.inflate(inflater, container, false)
        return binding.root
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
        mView = view

        swipeRefresh = binding.swiperefreshFitness
        recyclerView = binding.recyclerViewFitnessRooms

        swipeRefresh.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        recyclerView.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        swipeRefresh.setOnRefreshListener { getFitnessRooms() }

        // populate the title/date of the app bar
        initAppBar()

        // populate recyclerview
        getFitnessRooms()
    }

    private fun getFitnessRooms() {
        // displays banner if not connected
        if (!getConnected()) return

        Log.i("IDK BRO1", Thread.currentThread().name)

        try {

            mStudentLifeRf2.getFitnessRooms()?.subscribeOn(Schedulers.io())?.subscribe({ fitnessRooms ->
                    val rooms = fitnessRooms?.filterNotNull().orEmpty()
                    for (room in rooms) {
                        Log.i("Fitness Room${room.roomId}", "${room.roomName}")
                    }

                    Log.i("IDK BRO1", Thread.currentThread().name)

                    val sortedRooms = rooms.sortedBy { it.roomName }

                    dataModel = FitnessPreferenceViewModel(mStudentLifeRf2, sortedRooms)

                    mActivity.mNetworkManager.getAccessToken {
                        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
                        val context = mActivity.applicationContext
                        val bearerToken =
                            "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

                        Log.i("IDK BRO3", Thread.currentThread().name)

                        mStudentLifeRf2.getFitnessPreferences(bearerToken)?.subscribe({ favorites ->

                            Log.i("IDK BRO2", Thread.currentThread().name)

                            val favoriteRooms = favorites?.rooms?.filterNotNull().orEmpty()

                            for (roomId in favoriteRooms) {
                                dataModel.addId(roomId)
                            }
                            dataModel.updatePositionMap()

                            mActivity.runOnUiThread {
                                setAdapters()
                            }
                        }, { throwable ->

                            mActivity.runOnUiThread {
                                // empty preferences
                                setAdapters()
                                Log.e(
                                    "Pottruck Fragment",
                                    "Could not load Fitness Preferences",
                                    throwable,
                                )
                            }


                        })
                    }

                }, {
                    Log.e("PottruckFragment", "Error getting fitness rooms", it)
                    mActivity.runOnUiThread {
                        Log.e("Fitness", "Could not load Pottruck page", it)
                        binding.loadingPanel.root.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapters() {
        favoritesAdapter = FitnessAdapter(true, dataModel)
        otherAdapter = FitnessAdapter(false, dataModel)

        favoriteHeaderAdapter = FitnessHeaderAdapter("Favorites")
        otherHeaderAdapter = FitnessHeaderAdapter("Other Facilities")

        val concatAdapter =
            ConcatAdapter(
                favoriteHeaderAdapter,
                favoritesAdapter,
                otherHeaderAdapter,
                otherAdapter,
            )

        recyclerView.adapter = concatAdapter
        binding.loadingPanel.root.visibility = View.GONE
        swipeRefresh.isRefreshing = false

        // set click listener for favorites button
        val fitnessPref: ImageView = binding.fitnessPreferences
        fitnessPref.setOnClickListener {
            dataModel.savePreferences()
            val prefDialog =
                FitnessPreferencesFragment(
                    dataModel,
                    object : CloseListener {
                        override fun updateAdapters() {
                            favoritesAdapter.notifyDataSetChanged()
                            otherAdapter.notifyDataSetChanged()
                        }
                    },
                )
            prefDialog.show(mActivity.supportFragmentManager, "Fitness Preferences Dialog")
        }
    }

    /**
     * Checks if app is connected to internet. If not, it displays a banner
     * @return true if connected to internet and false otherwise
     */
    private fun getConnected(): Boolean {
        // displays banner if not connected
        val connectionToolbar: Toolbar = binding.toolbarFitnessConnection
        val connectionMessage: TextView = binding.textFitnessConnectionMessage

        if (!isOnline(context)) {
            connectionToolbar.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.darkRedBackground,
                ),
            )
            connectionMessage.text = getString(R.string.internet_error)
            connectionToolbar.visibility = View.VISIBLE
            binding.loadingPanel.root.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            return false
        }
        connectionToolbar.visibility = View.GONE
        return true
    }

    /**
     * Initialize the app bar of the fragment and
     * fills in the textViews for the title/date
     */
    private fun initAppBar() {
        val appBarLayout: AppBarLayout = binding.appbarHomeHolder
        val titleView: TextView = binding.titleView
        val dateView: TextView = binding.dateView

        (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        titleView.text = getString(R.string.fitness)
        dateView.text = Utils.getCurrentSystemTime()
    }
}
