package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import okhttp3.MultipartBody
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.Callback

class SublettingViewModel (private val activity: Activity, private val studentLife: StudentLife) {

    //store posted sublets
    //this updates live data - fragment observes, rv cals

    var postedSubletsList = MutableLiveData<ArrayList<Sublet>>()


    fun postSublet(mActivity : MainActivity, sublet : Sublet, callback: (Sublet?) -> Unit) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.createSublet(bearerToken, sublet,
                    object : Callback<Sublet> {
                        override fun success(t: Sublet?, response: Response?) {
                            Log.i("Subletting View Model", "sublet posted")
                            callback(sublet)
                        }

                        override fun failure(error: RetrofitError?) {
                            Log.e("Subletting View Model", "Error posting sublet " +
                                    "$error", error
                            )
                            Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()


                        }

                    })
        }


    }

    fun postImage(mActivity : MainActivity, subletID : Int, sublet : MultipartBody.Part, image : MultipartBody.Part) {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            studentLife.createImage(bearerToken, subletID, sublet, image, object : Callback<Sublet> {
                override fun success(t: Sublet?, response: Response?) {
                    Log.i("Subletting View Model", "sublet image posted")
                }

                override fun failure(error: RetrofitError?) {
                    Log.e("Subletting View Model", "Error posting sublet iamge " +
                            "$error", error
                    )
                    Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()


                }

            })


        }


    }

    fun getPostedSublets(mActivity : MainActivity) {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.getPostedSublets(bearerToken, false).subscribe({ sublets ->
                mActivity.runOnUiThread {
                    postedSubletsList.value = sublets as ArrayList<Sublet>
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                        "Posted Sublet Fragment",
                        "Could not load Posted Sublets",
                        throwable
                    )
                }
            })
        }
    }


    fun deleteSublet(mActivity: MainActivity, id: Int) {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.deleteSublet(bearerToken, id, object : Callback<Sublet> {
                override fun success(t: Sublet?, response: Response?) {
                    Log.i("Subletting View Model", "sublet deleted")
                }

                override fun failure(error: RetrofitError?) {
                    Log.e("Subletting View Model", "Error deleting sublet $error", error)
                    Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun editSublet(mActivity : MainActivity, sublet : Sublet, id: Int, callback: (Sublet?) -> Unit) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.editSublet(bearerToken, id, sublet,
                object : Callback<Sublet> {
                    override fun success(t: Sublet?, response: Response?) {
                        Log.i("Subletting View Model", "sublet edited")
                        callback(sublet)
                    }

                    override fun failure(error: RetrofitError?) {
                        Log.e("Subletting View Model", "Error editing sublet " +
                                "$error", error
                        )
                        Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()


                    }

                })
        }


    }




    fun getSublet(position : Int) : Sublet {
        return postedSubletsList.value?.get(position) ?: Sublet() // Provide a default value if needed
    }


}