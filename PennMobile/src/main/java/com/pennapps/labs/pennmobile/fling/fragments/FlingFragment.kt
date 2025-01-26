package com.pennapps.labs.pennmobile.fling.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentFlingBinding
import com.pennapps.labs.pennmobile.fling.adapters.FlingRecyclerViewAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FlingFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentFlingBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity = activity as MainActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
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
                val url =
                    "https://docs.google.com/forms/d/e/1FAIpQLSexkehYfGgyAa7RagaCl8rze4KUKQSX9TbcvvA6iXp34TyHew/viewform"
                val builder = Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(mActivity, Uri.parse(url))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFlingBinding.inflate(inflater, container, false)
        val view = binding.root
        val labs = MainActivity.studentLifeInstance
        try {
            labs
                .getFlingEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { flingEvents ->
                        flingEvents?.filterNotNull()?.let {
                            binding.flingFragmentRecyclerview.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            binding.flingFragmentRecyclerview.adapter =
                                FlingRecyclerViewAdapter(context, it)
                        }
                    },
                    {
                        Toast
                            .makeText(
                                activity,
                                "Could not retrieve Spring Fling schedule",
                                Toast.LENGTH_LONG,
                            ).show()
                    },
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onResume() {
        super.onResume()
        val mActivity: MainActivity = activity as MainActivity
        mActivity.removeTabs()
        mActivity.setTitle(R.string.spring_fling)
    }
}
