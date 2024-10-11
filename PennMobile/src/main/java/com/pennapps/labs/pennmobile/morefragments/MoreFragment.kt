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
import com.pennapps.labs.pennmobile.databinding.FragmentMoreBinding
import com.pennapps.labs.pennmobile.utils.Utils

class MoreFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentMoreBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val v = binding.root
        binding.dateView.text = Utils.getCurrentSystemTime()
        (
            binding.appbarHome.layoutParams
                as CoordinatorLayout.LayoutParams
        ).behavior = ToolbarBehavior()
        return v
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

    override fun onResume() {
        val initials =
            PreferenceManager
                .getDefaultSharedPreferences(mActivity)
                .getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            binding.initials.text = initials
        } else {
            binding.profileBackground.setImageDrawable(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
