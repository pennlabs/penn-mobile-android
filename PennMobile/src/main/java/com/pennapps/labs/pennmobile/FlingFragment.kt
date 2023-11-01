package com.pennapps.labs.pennmobile

import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.FlingRecyclerViewAdapter
import com.pennapps.labs.pennmobile.databinding.FragmentFlingBinding


class FlingFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    private var _binding : FragmentFlingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity = activity as MainActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "7")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Spring Fling")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fling_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            android.R.id.home -> {
                mActivity.onBackPressed()
                return true
            }
            R.id.fling_raffle -> {
                val url = "https://docs.google.com/forms/d/e/1FAIpQLSexkehYfGgyAa7RagaCl8rze4KUKQSX9TbcvvA6iXp34TyHew/viewform"
                val builder = Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(mActivity, Uri.parse(url))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFlingBinding.inflate(inflater, container, false)
        val view = binding.root
        val labs = MainActivity.studentLifeInstance
        labs.flingEvents.subscribe({ flingEvents ->
            activity?.runOnUiThread {
                binding.flingFragmentRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.flingFragmentRecyclerview.adapter = FlingRecyclerViewAdapter(context, flingEvents)
            }
        }, { activity?.runOnUiThread { Toast.makeText(activity, "Could not retrieve Spring Fling schedule", Toast.LENGTH_LONG).show() } })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onResume() {
        super.onResume()
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.removeTabs()
        mActivity?.setTitle(R.string.spring_fling)
    }
}
