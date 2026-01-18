package com.pennapps.labs.pennmobile.dining.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.compose.utils.NetworkUtils
import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.di.AppScope
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningRequest
import com.pennapps.labs.pennmobile.dining.fragments.DiningFragment.Companion.createHall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * TODO: Split DiningRepo into DiningRepo and FavouriteDiningRepo
 */
class DiningRepoImpl
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val applicationContext: Context,
        @AppScope private val appScope: CoroutineScope,
        private val studentLife: StudentLife,
        private val oAuth2NetworkManager: OAuth2NetworkManager,
    ) : DiningRepo {
        private val _favouriteDiningHalls = MutableStateFlow<List<Int>>(emptyList())
        override val favouriteDiningHalls: StateFlow<List<Int>> = _favouriteDiningHalls

        private val _allDiningHalls = MutableStateFlow<List<DiningHall>>(emptyList())
        override val allDiningHalls: StateFlow<List<DiningHall>> = _allDiningHalls

        override suspend fun fetchAllDiningHalls() {
            studentLife
                .venues()
                .subscribeOn(Schedulers.io())
                .subscribe { venues ->
                    val diningHalls =
                        venues
                            ?.map { venue -> createHall(venue ?: return@map null) }
                            ?.filterNotNull() ?: return@subscribe

                    _allDiningHalls.value = diningHalls
                }
        }

        override suspend fun fetchFavouriteDiningHalls() {
            oAuth2NetworkManager
                .doWhileAuthenticated(sharedPreferences, applicationContext) { bearerToken ->
                    studentLife
                        .getDiningPreferences(bearerToken)
                        .subscribeOn(Schedulers.io())
                        .subscribe { preferences ->
                            Log.d("DiningRepoImpl", "fetchFavouriteDiningHalls: $preferences")

                            _favouriteDiningHalls.value =
                                preferences?.preferences?.filter { it.id != null }?.map { it.id as Int }
                                    ?: listOf()
                        }
                }
        }

        private suspend fun updateFavouriteDiningHalls(favouriteDiningHalls: ArrayList<Int>): Result<Unit> {
            Log.d("DiningRepoImpl", "updateFavouriteDiningHalls: $favouriteDiningHalls")

            return try {
                val accessToken = oAuth2NetworkManager.getAccessToken()

                if (accessToken == null) {
                    return Result.Error(NetworkUtils.LOG_IN_TO_FAVOURITES)
                }
                val bearerToken = "Bearer $accessToken"

                val response =
                    studentLife.sendDiningPref(
                        bearerToken = bearerToken,
                        body = DiningRequest(favouriteDiningHalls),
                    )

                Log.d("DiningRepoImpl", "studentLife.sendDiningPref: ${response.body()}")
                fetchFavouriteDiningHalls()

                Result.Success(Unit)
            } catch (httpException: HttpException) {
                if (httpException.code() == 403) {
                    return Result.Error(NetworkUtils.LOG_IN_TO_FAVOURITES)
                } else {
                    Result.Error("Server returned an error: ${httpException.code()}.")
                }
            } catch (e: UnknownHostException) {
                // Specifically catches the error for when the device is offline (no internet)
                Log.e("DiningRepoImpl", "Device offline.", e)
                return Result.Error("You are offline. Please check your internet connection.")
            } catch (e: IOException) {
                // Catches other network I/O issues, like a timeout or connection reset
                Log.e("DiningRepoImpl", "Network IO error.", e)
                return Result.Error("A network error occurred. Please try again.")
            } catch (e: Exception) {
                // A general catch-all for any other unexpected exceptions
                Log.e("DiningRepoImpl", "An unexpected error occurred.", e)
                return Result.Error("An unexpected error occurred.")
            }
        }

        override suspend fun addToFavouriteDiningHalls(id: Int): Result<Unit> {
            val favouriteDiningHalls =
                ArrayList<Int>().apply {
                    addAll(favouriteDiningHalls.value)
                    add(id)
                }

            Log.d("DiningRepoImpl", "addToFavouriteDiningHalls: $favouriteDiningHalls")
            return updateFavouriteDiningHalls(favouriteDiningHalls)
        }

        override suspend fun removeFromFavouriteDiningHalls(id: Int): Result<Unit> {
            val favouriteDiningHalls =
                ArrayList<Int>().apply {
                    addAll(favouriteDiningHalls.value)
                    remove(id)
                }

            Log.d("DiningRepoImpl", "removeFromFavouriteDiningHalls: $favouriteDiningHalls")
            return updateFavouriteDiningHalls(favouriteDiningHalls)
        }
    }
