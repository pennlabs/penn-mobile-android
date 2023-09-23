package com.pennapps.labs.pennmobile

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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import com.pennapps.labs.pennmobile.adapters.FitnessHeaderAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.FitnessPreferenceViewModel
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils


class PottruckFragment : Fragment() {
    private lateinit var mActivity : MainActivity
    private lateinit var mStudentLife : StudentLife

    private lateinit var mView: View
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var loadingPanel : View

    private lateinit var dataModel : FitnessPreferenceViewModel
    private lateinit var favoritesAdapter : FitnessAdapter
    private lateinit var otherAdapter : FitnessAdapter
    private lateinit var favoriteHeaderAdapter : FitnessHeaderAdapter
    private lateinit var otherHeaderAdapter : FitnessHeaderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pottruck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view

        swipeRefresh = view.findViewById(R.id.swiperefresh_fitness)
        recyclerView = view.findViewById(R.id.recycler_view_fitness_rooms)
        loadingPanel = view.findViewById(R.id.loadingPanel)

        swipeRefresh.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        recyclerView.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        swipeRefresh.setOnRefreshListener { getFitnessRooms(view) }

        // populate the title/date of the app bar
        initAppBar()

        // populate recyclerview
        getFitnessRooms(view)
    }

    private fun getFitnessRooms(view: View) {
        //displays banner if not connected
        if (!getConnected()) return

        mStudentLife.fitnessRooms
            .subscribe({ fitnessRooms ->
                for (room in fitnessRooms) {
                    Log.i("Fitness Room${room.roomId}", "${room.roomName}")
                }
                val sortedRooms = fitnessRooms.sortedBy {it.roomName}

                dataModel = FitnessPreferenceViewModel(sortedRooms)

                mActivity.runOnUiThread {

                    favoritesAdapter = FitnessAdapter(true, dataModel)
                    otherAdapter = FitnessAdapter(false, dataModel)

                    favoriteHeaderAdapter = FitnessHeaderAdapter("Favorites")
                    otherHeaderAdapter = FitnessHeaderAdapter("Other Facilities")

                    val concatenated = ConcatAdapter(favoriteHeaderAdapter, favoritesAdapter,
                        otherHeaderAdapter, otherAdapter)

                    recyclerView.adapter = concatenated
                    loadingPanel.visibility = View.GONE
                    swipeRefresh.isRefreshing = false

                    // set click listener for favorites button
                    val fitnessPref : ImageView = view.findViewById(R.id.fitness_preferences)
                    fitnessPref.setOnClickListener {
                        dataModel.savePreferences()
                        val prefDialog = FitnessPreferencesFragment(dataModel, object: CloseListener{
                            override fun updateAdapters() {
                                favoritesAdapter.notifyDataSetChanged()
                                otherAdapter.notifyDataSetChanged()
                            }
                        })
                        prefDialog.show(mActivity.supportFragmentManager, "Fitness Preferences Dialog")
                    }
                }
            }, {
                Log.e("PottruckFragment", "Error getting fitness rooms", it)
                mActivity.runOnUiThread {
                    Log.e("Fitness", "Could not load Pottruck page", it)
                    loadingPanel.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                }
            })
    }

    /**
     * Checks if app is connected to internet. If not, it displays a banner
     * @return true if connected to internet and false otherwise
     */
    private fun getConnected(): Boolean {
        //displays banner if not connected
        val connectionToolbar : Toolbar = mView.findViewById(R.id.toolbar_fitness_connection)
        val connectionMessage : TextView = mView.findViewById(R.id.text_fitness_connection_message)

        if (!isOnline(context)) {
            connectionToolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkRedBackground))
            connectionMessage.text = getString(R.string.internet_error)
            connectionToolbar.visibility = View.VISIBLE
            loadingPanel.visibility = View.GONE
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
        val appBarLayout : AppBarLayout = mView.findViewById(R.id.appbar_home_holder)
        val titleView : TextView = mView.findViewById(R.id.title_view)
        val dateView : TextView = mView.findViewById(R.id.date_view)

        (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        titleView.text = getString(R.string.fitness)
        dateView.text = Utils.getCurrentSystemTime()
    }
}