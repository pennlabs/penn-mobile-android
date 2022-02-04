package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.adapters.DiningSettingsAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.DiningHall
import kotlinx.android.synthetic.main.fragment_dining_preferences.*
import kotlinx.android.synthetic.main.fragment_dining_preferences.view.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.fragment_dining_preferences.view.*
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import rx.Observable
import java.util.*

class DiningSettingsFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    private lateinit var halls: List<DiningHall>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity = activity as MainActivity
        mActivity.title = "Select Favorites"
        mStudentLife = MainActivity.studentLifeInstance
        mStudentLife = MainActivity.studentLifeInstance
        mActivity.toolbar.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_dining_preferences, container, false)
        v.dining_hall_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getDiningHalls()
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dining_preferences, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mActivity.onBackPressed()
                return true
            }
            R.id.save_button -> {
                saveDiningPreferences()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDiningHalls() {
        // Map each item in the list of venues to a Venue Observable, then map each Venue to a DiningHall Observable
        mStudentLife.venues()
                .flatMap { venues -> Observable.from(venues) }
                .flatMap { venue ->
                    val hall = DiningFragment.createHall(venue)
                    Observable.just(hall)
                }
                .toList()
                .subscribe({ diningHalls ->
                    mActivity.runOnUiThread {
                        halls = diningHalls
                        val adapter = DiningSettingsAdapter(diningHalls)
                        dining_hall_rv.adapter = adapter
                    }
                }, {
                    Log.e("DiningSettings", "error fetching dining halls")
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mActivity.toolbar.visibility = View.GONE
    }

    private fun saveDiningPreferences() {
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val favoriteDiningHalls = ArrayList<Int>()

        for (hall in halls) {
            if (sp.getBoolean(hall.name, false)) {
                favoriteDiningHalls.add(hall.id)
            }
        }

        //preferences must be in the form of 1,2,3 (exclude brackets)
        var apiPreparedString = favoriteDiningHalls.toString()
        apiPreparedString = apiPreparedString.substring(1, apiPreparedString.length - 1)

        mStudentLife.sendDiningPref(OAuth2NetworkManager(mActivity).getDeviceId(), apiPreparedString,
                object : ResponseCallback() {
            override fun success(response: Response) {
                mActivity.onBackPressed()
            }

            override fun failure(error: RetrofitError) {
                Log.e("Dining", "Error saving dining preferences: $error")
                Toast.makeText(mActivity, "Error saving dining preferences", Toast.LENGTH_SHORT).show()
            }
        })
    }


}