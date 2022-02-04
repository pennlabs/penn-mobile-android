package com.pennapps.labs.pennmobile.adapters

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.components.sneaker.Utils.convertToDp
import com.squareup.picasso.Picasso
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.home_base_card.view.*
import kotlinx.android.synthetic.main.home_news_card.view.*
import kotlinx.android.synthetic.main.home_post_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rx.Observable


class HomeAdapter(private var cells: ArrayList<HomeCell>) :
        RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife

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
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = mContext as MainActivity

        return when (viewType) {
            NEWS -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_news_card, parent, false))
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

        mStudentLife.venues()
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
        holder.itemView.home_news_title.text = info?.title
        holder.itemView.home_news_subtitle.text = info?.subtitle
        holder.itemView.home_news_timestamp.text = info?.timestamp?.trim()

        Picasso.get()
                .load(info?.imageUrl)
                .fit()
                .centerCrop()
                .into(holder.itemView.home_news_iv)

        /** Adds dynamically generated accent color from the fetched image to the news card */
        var accentColor: Int =  getColor(mContext, R.color.black)
        GlobalScope.launch(Dispatchers.Default) {
            val bitmap = Picasso.get().load(info?.imageUrl).get()

            // Create palette from bitmap
            fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
            val vibrantSwatch: Palette.Swatch? = createPaletteSync(bitmap).darkVibrantSwatch
            vibrantSwatch?.rgb?.let { accentColor = it }

            mActivity.runOnUiThread {
                // Change all the components to match the accent color palette
                vibrantSwatch?.titleTextColor?.let {
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.itemView.news_card_logo.drawable),
                            ColorUtils.setAlphaComponent(it, 150))
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.itemView.news_info_icon.drawable), it)
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.itemView.dot_divider.drawable), it)
                    holder.itemView.button.setTextColor(ColorUtils.setAlphaComponent(it, 150))
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.itemView.button.background), it)
                    holder.itemView.home_news_title.setTextColor(ColorUtils.setAlphaComponent(it, 150))
                    holder.itemView.home_news_subtitle.setTextColor(it)
                    holder.itemView.home_news_timestamp.setTextColor(it)
                }
                holder.itemView.news_card_container.background = BitmapDrawable(
                        holder.view.resources,
                        bitmap)
                holder.itemView.blurView
                        .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))
            }
        }

        /** Logic for the more info button on the news card */
        holder.itemView.news_info_icon.setOnClickListener {
            when (holder.itemView.home_news_subtitle.visibility) {
                View.GONE -> {
                    holder.itemView.home_news_subtitle.visibility = View.VISIBLE
                    holder.itemView.home_news_title.setPadding(0, 0, 0, 0)
                    holder.itemView.blurView
                            .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 250))
                }
                View.VISIBLE -> {
                    holder.itemView.home_news_subtitle.visibility = View.GONE
                    holder.itemView.home_news_title.setPadding(0, 0, 0, convertToDp(mContext, 8f))
                    holder.itemView.blurView
                            .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))
                }
            }
        }

        /** Sets up blur view on news card */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.itemView.blurView.setupWith(holder.itemView.news_card_container)
                    .setFrameClearDrawable(ColorDrawable(getColor(mContext, R.color.white)))
                    .setBlurAlgorithm(RenderScriptBlur(mContext))
                    .setBlurRadius(25f)
                    .setHasFixedTransformationMatrix(true)
        } else {
            holder.itemView.blurView.setBackgroundColor(ColorUtils
                    .setAlphaComponent(getColor(mContext, R.color.black), 225))
        }

        holder.itemView.button.setOnClickListener {

            val url = info?.articleUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(
                    mContext,
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit)
            CustomTabsClient.bindCustomTabsService(
                    mContext,
                    NewsFragment.CUSTOM_TAB_PACKAGE_NAME, connection)

            if (mContext.isChromeCustomTabsSupported()) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem(
                        "Share", PendingIntent.getActivity(
                        mContext, 0,
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
        eventList.reverse()

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

        val params : ConstraintLayout.LayoutParams =
                holder.itemView.home_card_rv.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.marginStart = 0
        }

        holder.itemView.home_card_rv.layoutParams = params

        mStudentLife.room(roomID).subscribe({ room ->
            mActivity.runOnUiThread {
                holder.itemView.home_card_title.text = room.name
                val rooms = arrayListOf(room)
                holder.itemView.home_card_rv.adapter = LaundryRoomAdapter(mContext, rooms, null, true)
            }

        }, { throwable -> mActivity.runOnUiThread { throwable.printStackTrace() } })
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
        val info = cell.info
        holder.itemView.home_post_title.text = info?.title
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

            if (mContext.isChromeCustomTabsSupported()) {
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

    /** Checks if the chrome tab is supported on the current device. */
    private fun Context.isChromeCustomTabsSupported(): Boolean {
        val serviceIntent = Intent("android.support.customtabs.action.CustomTabsService")
        serviceIntent.setPackage("com.android.chrome")
        val resolveInfos = this.packageManager.queryIntentServices(serviceIntent, 0)
        return resolveInfos.isNotEmpty()
    }

}