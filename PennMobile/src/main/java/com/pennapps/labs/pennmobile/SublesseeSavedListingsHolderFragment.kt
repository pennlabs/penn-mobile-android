package com.pennapps.labs.pennmobile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pennapps.labs.pennmobile.adapters.SublesseeSavedAdapter
import com.pennapps.labs.pennmobile.adapters.SublettingListAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.SublesseeViewModel
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingModel
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeSavedListingsHolderBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeMarketplaceBinding

class SublesseeSavedListingsHolderFragment (): Fragment() {

    private var _binding : FragmentSublesseeSavedListingsHolderBinding? = null
    private val binding get() = _binding!!

    //recyclerview adapters and layout manager
    lateinit var sublettingRecyclerView: RecyclerView
    lateinit var newLayoutManager: GridLayoutManager
    lateinit var sublettingList: ArrayList<Sublet>
    lateinit var myAdapter: SublesseeSavedAdapter
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var dataModel: SublesseeViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var propertiesList: List<String>

    //api manager
    private lateinit var mStudentLife: StudentLife
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

        dataModel = SublesseeViewModel(mActivity, mStudentLife)
        //dataModel.listSublets(mActivity)
        dataModel.getFavoriteSublets(mActivity)

        val bundle = Bundle()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSublesseeSavedListingsHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sublettingRecyclerView = binding.sublesseeSavedList
        newLayoutManager = GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false)
        sublettingRecyclerView.layoutManager = newLayoutManager
        swipeRefreshLayout = binding.sublesseeSavedRefreshLayout

        /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val savedProperties = sharedPreferences.getStringSet("sublet_saved", HashSet<String>())
        propertiesList = savedProperties!!.toList() */

        myAdapter = SublesseeSavedAdapter(dataModel)
        dataModel.savedSublets.observe(viewLifecycleOwner) { sublets ->
            sublettingList = sublets
            Log.i("sublet saved", sublettingList.size.toString())
            myAdapter.notifyDataSetChanged()
        }

        sublettingRecyclerView.adapter = myAdapter

        swipeRefreshLayout.setOnRefreshListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}