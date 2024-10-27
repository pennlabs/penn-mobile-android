package com.pennapps.labs.pennmobile.more.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.ListFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.more.adapters.SupportAdapter
import com.pennapps.labs.pennmobile.more.classes.Contact

class SupportFragment : ListFragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate( savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val mListView = listView
        val contactsList: ArrayList<Contact> = ArrayList()
        contactsList.add(Contact("Penn Police General", "(215) 898-7297"))
        contactsList.add(Contact("Penn Police Emergencies/MERT", "(215) 573-3333"))
        contactsList.add(Contact("Penn Walk", "215-898-9255", "215-898-WALK"))
        contactsList.add(Contact("Penn Ride", "215-898-7433", "215-898-RIDE"))
        contactsList.add(Contact("Help Line", "215-898-4357", "215-898-HELP"))
        contactsList.add(Contact("CAPS", "(215) 898-7021"))
        contactsList.add(Contact("Special Services", "(215) 898-6600"))
        contactsList.add(Contact("Women's Center", "(215) 898-8611"))
        contactsList.add(Contact("Student Health Services", "(215) 746-3535"))
        contactsList.add(Contact("Penn Violence Protection", "https://secure.www.upenn.edu/vpul/pvp/gethelp"))

        val supportAdapter = SupportAdapter(mActivity.applicationContext, contactsList)
        mListView.adapter = supportAdapter
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_support, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = mActivity.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        toolbar.setNavigationIcon(R.drawable.ic_back_navigation)
        toolbar.setNavigationOnClickListener { mActivity.onBackPressed() }
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        mActivity.menuInflater.inflate(R.menu.phone_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.support_contacts_add -> {
                val frag = SaveContactsFragment()
                val fragmentManager = mActivity.supportFragmentManager
                fragmentManager
                    .beginTransaction()
                    .replace(R.id.save_contacts_fragment, frag, "SAVE_CONTACTS_FRAGMENT")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        toolbar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.support)
        mActivity.hideBottomBar()
        mActivity.setSelectedTab(MainActivity.MORE)
    }

    override fun onDestroyView() {
        toolbar.setNavigationOnClickListener(null)
        super.onDestroyView()
    }
}
