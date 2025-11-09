package com.pennapps.labs.pennmobile.dining.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.di.AppScope
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningRequest
import com.pennapps.labs.pennmobile.dining.fragments.DiningFragment.Companion.createHall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * TODO: Split DiningRepo into DiningRepo and FavouriteDiningRepo
 */
class DiningRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val applicationContext: Context,
    @AppScope private val appScope: CoroutineScope,
    private val studentLife: StudentLife,
    private val oAuth2NetworkManager: OAuth2NetworkManager
) : DiningRepo {

    private val _favouriteDiningHalls = MutableStateFlow<List<Int>>(emptyList())
    override val favouriteDiningHalls: StateFlow<List<Int>> = _favouriteDiningHalls

    private val _allDiningHalls = MutableStateFlow<List<DiningHall>>(emptyList())
    override val allDiningHalls: StateFlow<List<DiningHall>> = _allDiningHalls


    override suspend fun fetchAllDiningHalls() {
        studentLife.venues()
            .subscribeOn(Schedulers.io())
            .subscribe { venues ->
                val diningHalls = venues
                    ?.map { venue -> createHall(venue ?: return@map null) }
                    ?.filterNotNull() ?: return@subscribe

                _allDiningHalls.value = diningHalls
            }
    }

    override suspend fun fetchFavouriteDiningHalls() {
        oAuth2NetworkManager
            .doWhileAuthenticated(sharedPreferences, applicationContext) { bearerToken ->
                studentLife.getDiningPreferences(bearerToken)
                    .subscribeOn(Schedulers.io())
                    .subscribe { preferences ->
                        Log.d("DiningRepoImpl", "fetchFavouriteDiningHalls: $preferences")

                        _favouriteDiningHalls.value =
                            preferences?.preferences?.filter { it.id != null }?.map { it.id as Int }
                                ?: listOf()
                    }
            }
    }

    private fun updateFavouriteDiningHalls(favouriteDiningHalls: ArrayList<Int>) {
        Log.d("DiningRepoImpl", "updateFavouriteDiningHalls: $favouriteDiningHalls")

        oAuth2NetworkManager.getAccessToken {
            val bearerToken =
                "Bearer " + sharedPreferences.getString(
                    applicationContext.getString(R.string.access_token),
                    " "
                )

            appScope.launch(Dispatchers.IO) {
                val response = studentLife.sendDiningPref(
                    bearerToken = bearerToken,
                    body = DiningRequest(favouriteDiningHalls)
                )

                Log.d("DiningRepoImpl", "studentLife.sendDiningPref: ${response.body()}")
                fetchFavouriteDiningHalls()
            }
        }
    }

    override suspend fun addToFavouriteDiningHalls(id: Int) {
        val favouriteDiningHalls = ArrayList<Int>().apply {
            addAll(favouriteDiningHalls.value)
            add(id)
        }

        Log.d("DiningRepoImpl", "addToFavouriteDiningHalls: $favouriteDiningHalls")
        updateFavouriteDiningHalls(favouriteDiningHalls)
    }

    override suspend fun removeFromFavouriteDiningHalls(id: Int) {
        val favouriteDiningHalls = ArrayList<Int>().apply {
            addAll(favouriteDiningHalls.value)
            remove(id)
        }

        updateFavouriteDiningHalls(favouriteDiningHalls)
    }
}