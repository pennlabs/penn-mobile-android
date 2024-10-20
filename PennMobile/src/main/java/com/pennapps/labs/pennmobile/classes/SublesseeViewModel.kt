package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit.Callback
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.concurrent.CountDownLatch

class SublesseeViewModel(private val activity: Activity, private val studentLife: StudentLife) {

    var sublettingList = MutableLiveData<ArrayList<Sublet>>()
    var savedSublets = MutableLiveData<ArrayList<Sublet>>()
    var savedSubletIds = hashSetOf<Int>()

    fun getSublet(position : Int) : Sublet {
        return sublettingList.value?.get(position) ?: Sublet() // Provide a default value if needed
    }

    fun getSavedSublet(position : Int) : Sublet {
        return savedSublets.value?.get(position) ?: Sublet() // Provide a default value if needed
    }

    /* fun getSubletById(id: String, mActivity: MainActivity): Sublet {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        /* OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            studentLife.getSubletById(bearerToken, id).subscribe({ sublet ->
                mActivity.runOnUiThread {
                    if (savedSublets.value == null) {
                        savedSublets.value = ArrayList<Sublet>()
                    }
                    savedSublets.value?.add(sublet)
                    Log.i("saved sublets", savedSublets.value?.size.toString())
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                            "Sublessee Saved",
                            "Could not load saved Sublets",
                            throwable
                    )
                }
            })
        } */
        for (sublet in getSublettingList()!!) {
            if (id == sublet.id.toString()) {
                return sublet
            }
        }
        return Sublet()
    } */

    fun getSublettingList(): ArrayList<Sublet>? {
        return sublettingList.value
    }

    fun getSavedSubletsList(): ArrayList<Sublet>? {
        return savedSublets.value
    }

    fun listSublets(mActivity: MainActivity) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            studentLife.getSublets(bearerToken).subscribe({ sublets ->
                mActivity.runOnUiThread {
                    sublettingList.value = sublets as ArrayList<Sublet>
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                            "Sublessee Marketplace",
                            "Could not load Posted Sublets",
                            throwable
                    )
                }
            })
        }
        Log.i("sublets", getSublettingList()?.size.toString())
    }

    fun addSavedSublet(mActivity: MainActivity, id: Int, sublet: Sublet, callback: (Sublet?) -> Unit) {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.addFavoriteSublet(bearerToken, id, sublet, object : Callback<Sublet> {
                override fun success(t: Sublet?, response: Response?) {
                    Log.i("Sublessee View Model", "sublet added")
                    callback(sublet)
                }

                override fun failure(error: RetrofitError?) {
                    Log.e("Sublessee View Model", "Error favoriting sublet $error", error)
                    Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun getFavoriteSublets(mActivity: MainActivity) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            studentLife.getSubletFavorites(bearerToken).subscribe({ sublets ->
                mActivity.runOnUiThread {
                    Log.i("can access saved sublets", savedSublets.value.toString())
                    savedSublets.value = sublets as ArrayList<Sublet>
                    //returns list of ints?
                    Log.i("saved sublet size", savedSublets.value.toString())
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                            "Sublessee Favorites",
                            "Could not load saved Sublets",
                            throwable
                    )
                }
            })
        }
        Log.i("sublets", getSavedSubletsList()?.size.toString())
    }
}