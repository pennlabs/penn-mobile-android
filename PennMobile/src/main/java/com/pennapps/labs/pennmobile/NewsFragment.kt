package com.pennapps.labs.pennmobile

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.browser.customtabs.*
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.initials
import kotlinx.android.synthetic.main.fragment_news.profile_background
import java.util.ArrayList


class NewsFragment : ListFragment() {

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
    private lateinit var sharedPreferences: SharedPreferences

    internal inner class CustomListAdapter(@get:JvmName("getContext_") private val context: Context,
                                           newsNames: Array<String?>,
                                           private val news: Array<NewsSite>) : ArrayAdapter<String>(context, R.layout.fragment_news, newsNames) {

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val rowView = inflater.inflate(R.layout.news_list_item, null, true)

            val newsName = rowView.findViewById(R.id.news_name) as TextView
            val newsLogo = rowView.findViewById(R.id.news_card_logo) as ImageView
            val newsDetails = rowView.findViewById(R.id.news_details) as TextView

            newsName.text = news[position].name
            newsLogo.setImageResource(news[position].image)
            newsDetails.text = news[position].description
            return rowView
        }
    }

    internal inner class NewsSite(val name: String, val url: String, val description: String, val image: Int) {

        override fun toString() = name
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
                    override fun describeContents() = 0

                    override fun writeToParcel(parcel: Parcel, i: Int) { parcel.writeString(URLs[i]) }
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
        return resolveInfos.isNotEmpty()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val connection = NewsCustomTabsServiceConnection()
        context?.let { context ->
            isCustomTabsSupported = isChromeCustomTabsSupported(context)
        }
        setHasOptionsMenu(true)
        mListView = listView
        builder = CustomTabsIntent.Builder()
        share = Intent(Intent.ACTION_SEND)
        share?.type = "text/plain"
        builder?.setToolbarColor(0x3E50B4)
        context?.let { context ->
            builder?.setStartAnimations(context,
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit)
            CustomTabsClient.bindCustomTabsService(context,
                    CUSTOM_TAB_PACKAGE_NAME, connection)
        }

        addNews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        setHasOptionsMenu(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "News")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        return view
    }

    private fun addNews() {

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionNews?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_news?.text = resources.getString(R.string.internet_error)
            internetConnectionNews?.visibility = View.VISIBLE
        } else {
            internetConnectionNews?.visibility = View.GONE
        }

        val dpDescription = "The Daily Pennsylvanian is the independent student newspaper of the University of Pennsylvania."
        val thirtyFourDescription = "34th Street Magazine is the DP's arts and entertainment weekly magazine."
        val utbDescription = "Under The Button is Penn's 24/7 news and entertainment blog, known for its signature humor, gossip and snarky features."
        val dp = NewsSite("The Daily Pennsylvanian", "http://www.thedp.com/",
                dpDescription, R.drawable.thedp)
        var thirtyFour = NewsSite("34th Street", "http://www.34st.com/",
                thirtyFourDescription, R.drawable.thirtyfour)
        var utb = NewsSite("Under the Button",
                "https://www.underthebutton.com/", utbDescription, R.drawable.utb)

        if (Build.VERSION.SDK_INT > 28 && (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            thirtyFour = NewsSite("34th Street", "http://www.34st.com/",
                    thirtyFourDescription, R.drawable.thirtyfour_darkmode)
            utb = NewsSite("Under the Button",
                    "https://www.underthebutton.com/", utbDescription, R.drawable.utb_darkmode)
        }

        val allSites = arrayOf(dp, thirtyFour, utb)
        val newsUrls = arrayOfNulls<String>(allSites.size)
        for (i in newsUrls.indices) {
            newsUrls[i] = allSites[i].url
        }
        context?.let {context ->
            val adapter = CustomListAdapter(context, newsUrls, allSites)
            mListView?.setAdapter(adapter)
        }


    }


    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        l.let {l ->
            val url = l.getItemAtPosition(position) as String
                if (isCustomTabsSupported) {
                    share?.putExtra(Intent.EXTRA_TEXT, url)
                    builder?.addMenuItem("Share", PendingIntent.getActivity(context, 0,
                            share, PendingIntent.FLAG_CANCEL_CURRENT))
                    customTabsIntent = builder?.build()
                    activity?.let { activity ->
                        customTabsIntent?.launchUrl(activity, Uri.parse(url))
                    }
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                }
            }
      }

    override fun onResume() {
        super.onResume()
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.removeTabs()
        mActivity?.setTitle(R.string.news)
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            this.profile_background.setImageDrawable(
                    resources.getDrawable
                    (R.drawable.ic_guest_avatar, context?.theme))
        }
        mActivity?.setSelectedTab(MainActivity.MORE)
    }

    override fun onDestroyView() {
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.removeTabs()
        super.onDestroyView()
    }
}