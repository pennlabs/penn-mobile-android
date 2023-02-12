package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.api.StudentLifePolls
import kotlinx.android.synthetic.main.fragment_dining.*
import rx.Observable

class PollsFragment : Fragment() {
    private val TAG = "PollsFragment"
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLifePolls: StudentLifePolls
    var bearerToken = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLifePolls = MainActivity.StudentLifePollsInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        setHasOptionsMenu(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Polls")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        activity?.let { activity ->
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            //sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
            bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
        }

    }

    /*public fun getPolls(bearerToken : String){
        //Offline message

        //Observable commands needed
        val pollList = mStudentLifePolls.validPostsList(bearerToken)
            .flatMap { validPollsList -> Observable.from(validPollsList) }
            .flatMap { validPoll ->
                //val hall = DiningFragment.createHall(validPollsList)
                Log.d("TAG onner", validPoll.toString())
                Observable.just(validPoll)

            }
            .toList()
            .subscribe({ value -> Log.d("TAG", "oonNext: $value")},
                {error -> Log.d("TAG", "oonError: ${error}")},
                { Log.d("TAG", "doonezo ")})
        Log.d("TAG outer", pollList.toString())

    }*/

    private fun voteForPoll(){
        mStudentLifePolls.createPollVote(bearerToken, null
        )

    }

    private fun getHistory(){
        mStudentLifePolls.pollsHistory()

    }

    private fun getPopulations(){
        val popList = mStudentLifePolls.pollsPopulations(
            bearerToken
        )
            .flatMap { pollsPopulations -> Observable.from(pollsPopulations) }
            .flatMap { pollPopulation ->
                //val hall = DiningFragment.createHall(validPollsList)
                //Log.d("TAG inner2", pollPopulation.toString())
                Observable.just(pollPopulation)

            }
            .toList()
            .subscribe({ value -> Log.d("TAG", "onNext: $value")},
                {error -> Log.d("TAG", "onError: ${error}")},
                { Log.d("TAG", "donezo ")})


        Log.d("TAG outer2", popList.toString())

    }


}