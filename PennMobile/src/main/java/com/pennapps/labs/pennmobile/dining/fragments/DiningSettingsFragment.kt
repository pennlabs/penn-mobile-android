package com.pennapps.labs.pennmobile.dining.fragments

import StudentLife
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentDiningPreferencesBinding
import com.pennapps.labs.pennmobile.dining.adapters.DiningSettingsAdapter
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningRequest
import com.pennapps.labs.pennmobile.dining.fragments.DiningFragment.Companion.createHall
import com.pennapps.labs.pennmobile.home.classes.HomepageDataModel
import kotlinx.coroutines.launch
import rx.Observable
import rx.schedulers.Schedulers

class DiningSettingsFragment(
    private val dataModel: HomepageDataModel,
) : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife

    private lateinit var halls: List<DiningHall>
    private lateinit var toolbar: Toolbar

    private var _binding: FragmentDiningPreferencesBinding? = null
    val binding get() = _binding!!

    private lateinit var originalPreferences: List<Int>
    private var savedNewPrefs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity = activity as MainActivity
        mStudentLife = MainActivity.studentLifeInstance
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiningPreferencesBinding.inflate(inflater, container, false)
        val v = binding.root
        binding.diningHallRv.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false,
            )
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity.hideBottomBar()
        getDiningHalls()
        return v
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = mActivity.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        mActivity.title = "Select Favorites"
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        inflater.inflate(R.menu.dining_preferences, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toolbar.visibility = View.GONE
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
        originalPreferences = dataModel.getDiningHallPrefs()
        try {
            mStudentLife
                .venues()
                .subscribeOn(Schedulers.io())
                .flatMap { venues -> Observable.from(venues) }
                .flatMap { venue ->
                    venue?.let {
                        val hall = createHall(it)
                        Observable.just(hall)
                    } ?: Observable.empty()
                }.toList()
                .subscribe({ diningHalls ->
                    mActivity.runOnUiThread {
                        halls = diningHalls
                        try {
                            binding.diningHallRv.adapter = DiningSettingsAdapter(diningHalls)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
                }, {
                    Log.e("DiningSettings", "error fetching dining halls")
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        toolbar.visibility = View.GONE
        if (!savedNewPrefs) restoreOriginal()
        _binding = null
        super.onDestroyView()
    }

    private fun restoreOriginal() {
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)

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

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response =
                        mStudentLife.sendDiningPref(
                            bearerToken,
                            DiningRequest(favoriteDiningHalls),
                        )

                    if (response.isSuccessful) {
                        Log.i("Dining", "Dining preferences saved")
                        mActivity.onBackPressed()
                    } else {
                        val error = Exception(response.body()?.string() ?: "Unknown Error")
                        Log.e("Dining", "Error saving dining preferences: $error")
                        Toast
                            .makeText(
                                mActivity,
                                "Error saving dining preferences",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
