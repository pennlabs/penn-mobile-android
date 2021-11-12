package com.pennapps.labs.pennmobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.api.PlatformPolls
import kotlinx.android.synthetic.main.fragment_dining.*
import java.util.*

class PollsFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mPlatformPolls: PlatformPolls


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlatformPolls = MainActivity.PlatformPollsInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        setHasOptionsMenu(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Polls")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        //FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    private fun getPolls(){
        //Offline message
        if (!isOnline(context)) {
            internetConnectionDining?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_dining?.setText("Not Connected to Internet")
            internetConnectionDining?.visibility = View.VISIBLE
        } else {
            internetConnectionDining?.visibility = View.GONE
        }

        //Observable commands needed
        mPlatformPolls.validPollsList().toString()
    }

    private fun voteForPoll(){
        mPlatformPolls.createPollVote()

    }

    private fun getHistory(){
        mPlatformPolls.pollsHistory()

    }


}