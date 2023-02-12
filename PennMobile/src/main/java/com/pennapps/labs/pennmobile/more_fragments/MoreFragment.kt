package com.pennapps.labs.pennmobile.more_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class MoreFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        initAppBar(view)
        return view
    }

    private fun initAppBar(view: View) {
        view.date_view.text = Utils.getCurrentSystemTime()
        // Appbar behavior init
        (view.appbar_home.layoutParams
                as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

    }

    override fun onResume() {
        val initials = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            this.profile_background.setImageDrawable(
                    ResourcesCompat.getDrawable(resources,
                            R.drawable.ic_guest_avatar, context?.theme)
            )
        }
        super.onResume()
    }

}
