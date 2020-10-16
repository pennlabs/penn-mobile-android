package com.pennapps.labs.pennmobile.api

import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity

class CampusExpressNetworkManager(mActivity: MainActivity) {

    private var mPlatform = MainActivity.platformInstance
    private var mLabs = MainActivity.labsInstance
    private var mActivity = mActivity
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor = sp.edit()

    private val baseUrl = "https://prod.campusexpress.upenn.edu"
    private val campusExpressShibbolethUrl = "https://prod.campusexpress.upenn.edu/Shibboleth.sso/SAML2/POST"
    private val diningBalanceString = "dining/balance.jsp"
    private val housingString = "housing/view_assignment.jsp"

    fun getDiningBalance() {
        var mPennAuthRequestable = PennAuthRequestable(mActivity)
        mPennAuthRequestable.makeAuthRequest(baseUrl, diningBalanceString, campusExpressShibbolethUrl) {
            response, error ->
            if (error == null) {
                Log.d("Accounts", "get dining balance done! $response")
            } else {
                Log.e("Accounts", "error getting dining balance $error")
            }
        }
    }

    fun getHousing() {
        val mPennAuthRequestable = PennAuthRequestable(mActivity)
        mPennAuthRequestable.makeAuthRequest(baseUrl, housingString, campusExpressShibbolethUrl) {
            response, error ->
            if (error == null) {
                Log.d("Accounts", "get housing done! $response")
            } else {
                Log.e("Accounts", "Error getting housing: $error")
            }
        }
    }
}