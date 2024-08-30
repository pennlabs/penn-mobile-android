package com.pennapps.labs.pennmobile.morefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.initials
import kotlinx.android.synthetic.main.fragment_home.profile_background
import kotlinx.android.synthetic.main.fragment_home.view.appbar_home
import kotlinx.android.synthetic.main.fragment_home.view.date_view

class MoreFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        initAppBar(view)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.more_frame, PreferenceFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun initAppBar(view: View) {
        view.date_view.text = Utils.getCurrentSystemTime()
        // Appbar behavior init
        (
            view.appbar_home.layoutParams
                as CoordinatorLayout.LayoutParams
        ).behavior = ToolbarBehavior()
    }

    override fun onResume() {
        val initials =
            PreferenceManager
                .getDefaultSharedPreferences(mActivity)
                .getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            this.profile_background.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_guest_avatar,
                    context?.theme,
                ),
            )
        }
        mActivity.setSelectedTab(MainActivity.MORE)
        super.onResume()
    }
}
