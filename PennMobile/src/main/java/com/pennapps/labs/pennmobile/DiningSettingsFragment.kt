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
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.DiningRequest
import com.pennapps.labs.pennmobile.classes.HomepageDataModel
import com.pennapps.labs.pennmobile.databinding.FragmentDiningPreferencesBinding
import kotlinx.android.synthetic.main.include_main.toolbar

import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import rx.Observable

class DiningSettingsFragment(dataModel: HomepageDataModel) : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    private lateinit var halls: List<DiningHall>
    private val dataModel : HomepageDataModel = dataModel

    private var _binding : FragmentDiningPreferencesBinding? = null
    private val binding get() = _binding!!

    private var savedNewPrefs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity = activity as MainActivity
        mActivity.toolbar.visibility = View.VISIBLE
        mActivity.title = "Select Favorites"
        mStudentLife = MainActivity.studentLifeInstance
        mStudentLife = MainActivity.studentLifeInstance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiningPreferencesBinding.inflate(inflater, container, false)
        val v = binding.root
        binding.diningHallRv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity.hideBottomBar()
        getDiningHalls()
        return v
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dining_preferences, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mActivity.toolbar.visibility = View.GONE
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
                        binding.diningHallRv.adapter = DiningSettingsAdapter(diningHalls)
                    }
                }, {
                    Log.e("DiningSettings", "error fetching dining halls")
                })
    }

    override fun onDestroyView() {
        super.onDestroyView() 
        if (!savedNewPrefs) restoreOriginal()
        mActivity.toolbar.visibility = View.GONE
        _binding = null
    }

    private fun restoreOriginal() {
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val originalPreferences = dataModel.getDiningHallPrefs()

        val editor = sp.edit()
        for (hall in halls) {
            editor.putBoolean(hall.name, originalPreferences.contains(hall.id))
            editor.apply()
        }
    }

    private fun saveDiningPreferences() {
        savedNewPrefs = true
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val favoriteDiningHalls = ArrayList<Int>()

        for (hall in halls) {
            if (sp.getBoolean(hall.name, false)) {
                favoriteDiningHalls.add(hall.id)
            }
        }
        
        dataModel.updateDining(favoriteDiningHalls)

        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
            mStudentLife.sendDiningPref(bearerToken, DiningRequest(favoriteDiningHalls),
                object : ResponseCallback() {
                    override fun success(response: Response) {
                        Log.i("Dining", "Dining preferences saved")
                        mActivity.onBackPressed()
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Dining", "Error saving dining preferences: $error")
                        Toast.makeText(
                            mActivity,
                            "Error saving dining preferences",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }


}
