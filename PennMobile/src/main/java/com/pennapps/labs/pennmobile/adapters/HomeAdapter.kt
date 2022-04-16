package com.pennapps.labs.pennmobile.adapters

import android.app.PendingIntent
import android.content.*
import android.net.Uri
import androidx.browser.customtabs.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_base_card.view.*
import kotlinx.android.synthetic.main.home_post_card.view.*
import rx.Observable


class HomeAdapter(private var cells: ArrayList<HomeCell>)
    : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var mLabs: Labs

    private var mCustomTabsClient: CustomTabsClient? = null
    private var customTabsIntent: CustomTabsIntent? = null
    private var share: Intent? = null
    private var session: CustomTabsSession? = null
    private var builder: CustomTabsIntent.Builder? = null

    companion object {
        // Types of Home Cells
        private const val NOT_SUPPORTED = -1
        private const val RESERVATIONS = 0
        private const val DINING = 1
        private const val CALENDAR = 2
        private const val NEWS = 3
        private const val LAUNDRY = 5
        private const val GSR_BOOKING = 6
        private const val POST = 7
        private const val FEATURE = 8
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        mLabs = MainActivity.labsInstance
        mActivity = mContext as MainActivity

        return when (viewType) {
            NEWS -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_post_card, parent, false))
            }
            POST -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_post_card, parent, false))
            }
            FEATURE -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_post_card, parent, false))
            }
            NOT_SUPPORTED -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cell = cells[position]
        when (cell.type) {
            "reservations" -> bindHomeReservationsCell(holder, cell)
            "dining" -> bindDiningCell(holder, cell)
            "calendar" -> bindCalendarCell(holder, cell)
            "news" -> bindNewsCell(holder, cell)
            "laundry" -> bindLaundryCell(holder, cell)
            "gsr_booking" -> bindGsrBookingCell(holder, cell)
            "post" -> bindPostCell(holder, cell)
            "feature" -> bindFeatureCell(holder, cell)
            else -> Log.i("HomeAdapter", "Unsupported type of data at position $position")
        }
    }

    override fun getItemCount(): Int {
        return cells.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int {
        val cell = cells[position]
        if (cell.info?.isTest == true) {
            Log.i("HomeAdapter", "Test Portal post")
            return NOT_SUPPORTED
        }
        return when (cell.type) {
            "reservations" -> RESERVATIONS
            "dining" -> DINING
            "calendar" -> CALENDAR
            "news" -> NEWS
            "laundry" -> LAUNDRY
            "gsr_booking" -> GSR_BOOKING
            "post" -> POST
            "feature" -> FEATURE
            else -> NOT_SUPPORTED
        }
    }

    private fun bindHomeReservationsCell(holder: ViewHolder, cell: HomeCell) {
        val reservations = cell.reservations ?: ArrayList()
        holder.itemView.home_card_title.text = "Upcoming Reservations"
        holder.itemView.home_card_subtitle.text = "GSR RESERVATIONS"

        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)
        holder.itemView.home_card_rv.adapter = GsrReservationsAdapter(ArrayList(reservations))
    }

    private fun bindDiningCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Favorites"
        holder.itemView.home_card_subtitle.text = "DINING HALLS"
        holder.itemView.dining_prefs_btn.visibility = View.VISIBLE
        holder.itemView.dining_prefs_btn.setOnClickListener {
            mActivity.fragmentTransact(DiningSettingsFragment())
        }

        mLabs.venues()
                .flatMap { venues -> Observable.from(venues) }
                .flatMap { venue ->
                    val hall = DiningFragment.createHall(venue)
                    Observable.just(hall)
                }
                .toList()
                .subscribe { diningHalls ->
                    mActivity.runOnUiThread {
                        val favorites: ArrayList<DiningHall> = arrayListOf()
                        val favoritesIdList: List<Int>? = cell.info?.venues
                        diningHalls.forEach {
                            if (favoritesIdList?.contains(it.id) == true) {
                                favorites.add(it)
                            }
                        }
                        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                                LinearLayoutManager.VERTICAL, false)
                        holder.itemView.home_card_rv.adapter = DiningCardAdapter(favorites)
                    }
                }
    }

    private fun bindNewsCell(holder: ViewHolder, cell: HomeCell) {
        val info = cell.info
        holder.itemView.home_post_title.text = info?.title
        holder.itemView.home_post_subtitle.text = info?.subtitle
        holder.itemView.home_post_source.text = info?.source
        holder.itemView.home_post_timestamp.text = info?.timestamp

        Picasso.get().load(info?.imageUrl).fit().centerCrop().into(holder.itemView.home_post_iv)

        holder.itemView.home_post_card.setOnClickListener {

            val url = info?.articleUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(mContext,
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit)
            CustomTabsClient.bindCustomTabsService(mContext,
                    NewsFragment.CUSTOM_TAB_PACKAGE_NAME, connection)

            if (isChromeCustomTabsSupported(mContext)) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem("Share", PendingIntent.getActivity(mContext, 0,
                        share, PendingIntent.FLAG_CANCEL_CURRENT))
                customTabsIntent = builder?.build()
                customTabsIntent?.launchUrl(mActivity, Uri.parse(url))
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(mContext, browserIntent, null)
            }
        }
    }

    private fun bindCalendarCell(holder: ViewHolder, cell: HomeCell) {
        val events = cell.events ?: ArrayList()

        var i = events.size - 1
        val eventList: ArrayList<CalendarEvent> = ArrayList()
        while (i >= 0) {
            if (!events[i].name.isNullOrEmpty()) {
                eventList.add(events[i])
            }
            i--;
        }

        holder.itemView.home_card_title.text = "Upcoming Events"
        holder.itemView.home_card_subtitle.text = "UNIVERSITY NOTIFICATIONS"

        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)

        holder.itemView.home_card_rv.adapter = UniversityEventAdapter(eventList)
    }

    private fun bindCoursesCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Today's schedule"
        holder.itemView.home_card_subtitle.text = "COURSE SCHEDULE"
    }

    private fun bindLaundryCell(holder: ViewHolder, cell: HomeCell) {
        val roomID = cell.info?.roomId ?: 0
        holder.itemView.home_card_subtitle.text = "LAUNDRY"
        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)

        mLabs.room(roomID).subscribe({ room ->
            mActivity.runOnUiThread {
                holder.itemView.home_card_title.text = room.name
                val rooms = arrayListOf(room)
                holder.itemView.home_card_rv.adapter = LaundryRoomAdapter(mContext, rooms, null, true)
            }

        }, { throwable -> mActivity.runOnUiThread { throwable.printStackTrace() } } )
    }

    private fun bindGsrBookingCell(holder: ViewHolder, cell: HomeCell) {
        val buildings = cell.buildings ?: ArrayList()

        holder.itemView.home_card_title.text = "Book a GSR"
        holder.itemView.home_card_subtitle.text = "GROUP STUDY ROOMS"

        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)
        holder.itemView.home_card_rv.adapter = HomeGsrBuildingAdapter(ArrayList(buildings))
    }

    private fun bindPostCell(holder: ViewHolder, cell: HomeCell) {
        Log.d("TAG BINDER", "I am binding haw")
        val info = cell.info
        holder.itemView.home_post_title.text = "CERTIFIED HOOD CLASSIC"
        holder.itemView.home_post_subtitle.text = info?.subtitle
        holder.itemView.home_post_source.text = info?.source
        holder.itemView.home_post_timestamp.text = info?.timeLabel

        Picasso.get().load(info?.imageUrl).fit().centerCrop().into(holder.itemView.home_post_iv)

        holder.itemView.home_post_card.setOnClickListener {

            val url = info?.postUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(mContext,
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit)
            CustomTabsClient.bindCustomTabsService(mContext,
                    NewsFragment.CUSTOM_TAB_PACKAGE_NAME, connection)

            if (isChromeCustomTabsSupported(mContext)) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem("Share", PendingIntent.getActivity(mContext, 0,
                        share, PendingIntent.FLAG_CANCEL_CURRENT))
                customTabsIntent = builder?.build()
                customTabsIntent?.launchUrl(mActivity, Uri.parse(url))
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(mContext, browserIntent, null)
            }
        }
    }

    // Returns an announcement for a Penn Mobile feature, such as Spring Fling
    private fun bindFeatureCell(holder: ViewHolder, cell: HomeCell) {
        val info = cell.info
        holder.itemView.home_post_title?.text = info?.title
        holder.itemView.home_post_subtitle?.text = info?.description
        holder.itemView.home_post_source?.text = info?.source
        holder.itemView.home_post_timestamp?.text = info?.timestamp
        if (info?.imageUrl != null) {
            Picasso.get().load(info.imageUrl).fit().centerCrop().into(holder.itemView.home_post_iv)
        }

        // For now, we only use Feature cards for Spring Fling so we show the Fling Fragment
        holder.itemView.home_post_card.setOnClickListener {
            mActivity.fragmentTransact(FlingFragment())
        }
    }
        // Chrome custom tabs to launch news site

    internal inner class NewsCustomTabsServiceConnection : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            mCustomTabsClient = client
            mCustomTabsClient?.warmup(0)
            session = mCustomTabsClient?.newSession(null)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mCustomTabsClient = null
            session = null
            customTabsIntent = null
        }
    }

    private fun isChromeCustomTabsSupported(context: Context): Boolean {
        val serviceIntent = Intent("android.support.customtabs.action.CustomTabsService")
        serviceIntent.setPackage("com.android.chrome")
        val resolveInfos = context.packageManager.queryIntentServices(serviceIntent, 0)
        return resolveInfos.isNotEmpty()
    }

}