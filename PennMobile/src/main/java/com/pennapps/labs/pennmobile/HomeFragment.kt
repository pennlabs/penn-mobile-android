package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.HomeCell
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import android.provider.Settings.Secure
import androidx.core.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.graphics.PorterDuff
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.pennapps.labs.pennmobile.api.StudentLifePolls
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import kotlinx.android.synthetic.main.fragment_dining.*
import org.joda.time.LocalDateTime
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import rx.Observable


class HomeFragment : Fragment()  {

    private lateinit var mActivity: MainActivity
    //
    private lateinit var mStudentLifePolls: StudentLifePolls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "11")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)

        /////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        //Observable commands needed
        /*Log.d("Tag6", "hello")
        val pollList = MainActivity.mStudentLifePolls?.validPollsList()
            ?.flatMap { validPollsList -> Observable.from(validPollsList) }
            ?.flatMap { validPollsList ->
                //val hall = DiningFragment.createHall(validPollsList)
                Log.d("TAG7", "Inside method")
                Log.d("TAG8", validPollsList.toString())
                Observable.just(validPollsList)

            }
            ?.toList()
            ?.subscribe { validPollsList ->
                Log.d("TAG9", validPollsList.toString())
            }
        Log.d("TAG10", pollList.toString())

        val popList = MainActivity.mStudentLifePolls?.pollsPopulations()
            ?.flatMap { validPopList -> Observable.from(validPopList) }
            ?.flatMap { validPopList ->
                //val hall = DiningFragment.createHall(validPollsList)
                Log.d("TAGa7", "Inside method")
                Log.d("TAGa8", validPopList.toString())
                Observable.just(validPopList)

            }
            ?.toList()
            ?.subscribe { validPollsList ->
                Log.d("TAGa9", validPollsList.toString())
            }

        Log.d("TAGa10", popList.toString())

        */

        //////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////

        mStudentLifePolls = MainActivity.StudentLifePollsInstance
        var bearerToken = ""
        activity?.let { activity ->
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            //sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
            bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
        }

        if (!isOnline(context)) {
            internetConnectionDining?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_dining?.setText("Not Connected to Internet")
            internetConnectionDining?.visibility = View.VISIBLE
        } else {
            internetConnectionDining?.visibility = View.GONE
        }
        Log.d("TAG", "onCreate: $bearerToken")
        //Observable commands needed
        val pollList = mStudentLifePolls.validPollsList(bearerToken)
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
        ////////////////////////////////////
        //Post: Add it to the MainAct thing,
        mStudentLifePolls.createPollVote(bearerToken,
            object : Callback<PollVoteResult> {
                override fun success(t: PollVoteResult?, response: Response?) {
                    if (t != null) {
                        if (t.getResults() == true){
                            Log.d("TAg p", "success: I was a winner")
                        }
                        else{
                            Log.d("TAG p", "success: NVM ")
                        }
                    } else {
                        Log.d("TAG p", "success: nullo")
                    }
                }

                override fun failure(error: RetrofitError?) {
                    if (error != null) {
                        //hits here
                        Log.d("TAG p", "failure: $error")
                    } else {
                        Log.d("TAG p", "failure: wha")
                    }
                }

            }

        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.home_cells_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout.setOnRefreshListener { getHomePage() }

        getHomePage()


        //////////////////////////////////////////////////////////////////////
        //initAppBar(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getHomePage() {

        // get session id from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
        val accountID = sp.getString(getString(R.string.accountID), "")
        val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionHome?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message?.setText("Not Connected to Internet")
            home_cells_rv?.setPadding(0, 90, 0, 0)
            internetConnectionHome?.visibility = View.VISIBLE
        } else {
            internetConnectionHome?.visibility = View.GONE
            home_cells_rv?.setPadding(0, 0, 0, 0)
        }

        // get API data
        val labs = MainActivity.labsInstance
        labs.getHomePage(deviceID, accountID, sessionID).subscribe({ cells ->
            mActivity.runOnUiThread {
                val gsrBookingCell = HomeCell()
                gsrBookingCell.type = "gsr_booking"
                gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "VP Weigle")
                cells?.add(cells.size - 1, gsrBookingCell)
                home_cells_rv?.adapter = HomeAdapter(ArrayList(cells))
                loadingPanel?.visibility = View.GONE
                home_refresh_layout?.isRefreshing = false

            }
        }, { throwable ->
            mActivity.runOnUiThread {
                Log.e("Home", "Could not load Home page")
                throwable.printStackTrace()
                Toast.makeText(mActivity, "Could not load Home page", Toast.LENGTH_LONG).show()
                loadingPanel?.visibility = View.GONE
                internetConnectionHome?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
                internetConnection_message?.setText("Not Connected to Internet")
                internetConnectionHome?.visibility = View.VISIBLE
                home_refresh_layout?.isRefreshing = false
            }

        })
    }

    private val broadcastReceiver = object: BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val firstName = sp.getString(getString(R.string.first_name), null)
        if (firstName != null) {
            mActivity.setTitle("Welcome, $firstName!")
        } else {
            mActivity.setTitle(R.string.main_title)
        }
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(MainActivity.HOME)
        }
    }

}



