package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentGsrBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeViewBinding
import org.joda.time.DateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubletteeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubletteeFragment : Fragment() {


    //create binding
    private var _binding : FragmentSubletteeViewBinding? = null
    private val binding get() = _binding!!

    //api manager
    private lateinit var mStudentLife: StudentLife

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

        val bundle = Bundle()

        //edit this later for firebase
        /* bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GSR")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle) */

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSubletteeViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val marketplaceButton: Button = view.findViewById(R.id.sublettee_enter_subletting_button)

        marketplaceButton.setOnClickListener {
            //load new fragment, which will hold the subletting marketplace in whole
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SubletteeMarketplace())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

}