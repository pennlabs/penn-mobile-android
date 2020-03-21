package com.pennapps.labs.pennmobile.api

import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity

class CampusExpressNetworkManager(mActivity: MainActivity) {

    private var mPlatform = MainActivity.getPlatformInstance()
    private var mLabs = MainActivity.getLabsInstance()
    private var mActivity = mActivity
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor: SharedPreferences.Editor? = sp.edit()

    private val baseUrl = "https://prod.campusexpress.upenn.edu/"
    private val campusExpressShibbolethUrl = "https://prod.campusexpress.upenn.edu/Shibboleth.sso/SAML2/POST/"
    private val diningBalanceUrl = "https://prod.campusexpress.upenn.edu/dining/balance.jsp"
    private val housingUrl = "https://prod.campusexpress.upenn.edu/housing/view_assignment.jsp"

    fun getDiningBalance() {
        val mPennAuthRequestable = PennAuthRequestable(mActivity, baseUrl)
        mPennAuthRequestable.makeAuthRequest(diningBalanceUrl, campusExpressShibbolethUrl) {
            response, error ->
            if (error == null) {
                Log.d("Accounts", "get dining balance done! $response")
            } else {
                Log.e("Accounts", "Error getting dining balance: $error")
            }
        }
    }

    fun getHousing() {
        val mPennAuthRequestable = PennAuthRequestable(mActivity, baseUrl)
        mPennAuthRequestable.makeAuthRequest(housingUrl, campusExpressShibbolethUrl) {
            response, error ->
            if (error == null) {
                Log.d("Accounts", "get housing done! $response")
            } else {
                Log.e("Accounts", "Error getting housing: $error")
            }
        }
    }
}