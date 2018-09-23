package com.pennapps.labs.pennmobile

import android.app.ListFragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.customtabs.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.news_list_item.*
import java.util.ArrayList


class NewsFragmentKotlin : ListFragment() {

    companion object {
        @JvmStatic val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
    }

    private var mListView: ListView? = null
    private var mCustomTabsClient: CustomTabsClient? = null
    private var customTabsIntent: CustomTabsIntent? = null
    private var share: Intent? = null
    private var session: CustomTabsSession? = null
    private var builder: CustomTabsIntent.Builder? = null
    private var isCustomTabsSupported: Boolean = false

    internal inner class CustomListAdapter(@get:JvmName("getContext_") private val context: Context,
                                           newsNames: Array<String>,
                                           private val news: Array<NewsSite>) : ArrayAdapter<String>(context, R.layout.fragment_news, newsNames) {

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val rowView = inflater.inflate(R.layout.news_list_item, null, true)

            news_name.text = news[position].name
            news_logo.setImageResource(news[position].image)
            news_details.text = news[position].description
            return rowView

        }
    }

    internal inner class NewsSite(val name: String, val url: String, val description: String, val image: Int) {

        override fun toString(): String {
            return name
        }
    }

    internal inner class NewsCustomTabsServiceConnection : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            mCustomTabsClient = client
            mCustomTabsClient?.warmup(0)
            session = mCustomTabsClient?.newSession(null)
            val URLs = ArrayList<String>()
            val titles = ArrayList<String>()
            URLs.add("http://www.thedp.com/")
            URLs.add("http://www.34st.com/")
            URLs.add("https://www.underthebutton.com/")
            titles.add("The Daily Pennsylvanian")
            titles.add("34th Street")
            titles.add("Under the Button")
            val urlList = ArrayList<Bundle>()
            for (i in URLs.indices) {
                val bundle = Bundle()
                bundle.putParcelable(CustomTabsService.KEY_URL, object : Parcelable {
                    override fun describeContents(): Int {
                        return 0
                    }

                    override fun writeToParcel(parcel: Parcel, i: Int) {
                        parcel.writeString(URLs[i])
                    }
                })
                urlList.add(bundle)
            }
            session?.mayLaunchUrl(Uri.parse(URLs[0]), null, urlList)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mCustomTabsClient = null
            session = null
            customTabsIntent = null
        }
    }

    private fun isChromeCustomTabsSupported(context: Context): Boolean {
        val SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService"
        val serviceIntent = Intent(SERVICE_ACTION)
        serviceIntent.setPackage("com.android.chrome")
        val resolveInfos = context.packageManager.queryIntentServices(serviceIntent, 0)
        return !(resolveInfos == null || resolveInfos.isEmpty())
    }

    



}