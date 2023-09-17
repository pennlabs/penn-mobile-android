package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import com.pennapps.labs.pennmobile.api.StudentLife

class PottruckFragment : Fragment() {
    private lateinit var mActivity : MainActivity
    private lateinit var mStudentLife : StudentLife

    private lateinit var mView: View
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var loadingPanel : View

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
        swipeRefresh.setOnRefreshListener { getFitnessRooms() }

        getFitnessRooms()
    }

    private fun getFitnessRooms() {
        //displays banner if not connected
        if (!getConnected()) return

        mStudentLife.fitnessRooms
            .subscribe({ fitnessRooms ->
                for (room in fitnessRooms) {
                    Log.i("Fitness Room${room.roomId}", "${room.roomName}")
                }
                val sortedRooms = fitnessRooms.sortedBy {it.roomName}
                mActivity.runOnUiThread {
                    val adapter = FitnessAdapter(sortedRooms)
                    recyclerView.adapter = adapter
                    loadingPanel.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
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
}