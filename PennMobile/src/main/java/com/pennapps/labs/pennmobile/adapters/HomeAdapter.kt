package com.pennapps.labs.pennmobile.adapters

import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.customtabs.*
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.NewsFragment
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import kotlinx.android.synthetic.main.home_base_card.view.*
import kotlinx.android.synthetic.main.home_news_card.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class HomeAdapter(private var cells: ArrayList<HomeCell>)
    : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var mCustomTabsClient: CustomTabsClient? = null
    private var customTabsIntent: CustomTabsIntent? = null
    private var share: Intent? = null
    private var session: CustomTabsSession? = null
    private var builder: CustomTabsIntent.Builder? = null

    companion object {
        private const val NOT_SUPPORTED = -1
        private const val RESERVATIONS = 0
        private const val DINING = 1
        private const val CALENDAR = 2
        private const val NEWS = 3
        private const val COURSES = 4
        private const val LAUNDRY = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity

        return when (viewType) {
            NEWS -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_news_card, parent, false))
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
            "courses" -> bindCoursesCell(holder, cell)
            "laundry" -> bindLaundryCell(holder, cell)
            else -> Log.d("HomeAdapter", "Unsupported type of data at position " + position)
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
        return when (cell.type) {
            "reservations" -> RESERVATIONS
            "dining" -> DINING
            "calendar" -> CALENDAR
            "news" -> NEWS
            "courses" -> COURSES
            "laundry" -> LAUNDRY
            else -> NOT_SUPPORTED
        }
    }

    private fun bindHomeReservationsCell(holder: ViewHolder, cell: HomeCell) {
        val reservations = cell.reservations
        holder.itemView.home_card_title.text = "Upcoming Reservations"
        holder.itemView.home_card_subtitle.text = "GSR RESERVATIONS"

        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)
        holder.itemView.home_card_rv.adapter = GsrReservationsAdapter(ArrayList(reservations))
    }

    private fun bindDiningCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Favorites"
        holder.itemView.home_card_subtitle.text = "DINING HALLS"
    }

    private fun bindNewsCell(holder: ViewHolder, cell: HomeCell) {
        val info = cell.info
        holder.itemView.home_news_title.text = info?.title
        holder.itemView.home_news_subtitle.text = info?.source
        holder.itemView.home_news_timestamp.text = info?.timestamp

        Picasso.get().load(info?.imageUrl).fit().centerCrop().into(holder.itemView.home_news_iv)

        holder.itemView.home_news_card.setOnClickListener {

            val url = info?.articleUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(mContext,
                    android.support.design.R.anim.abc_popup_enter,
                    android.support.design.R.anim.abc_popup_exit)
            CustomTabsClient.bindCustomTabsService(mContext,
                    NewsFragment.CUSTOM_TAB_PACKAGE_NAME, connection)

            if (isChromeCustomTabsSupported(mContext)) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem("Share", PendingIntent.getActivity(mContext, 0,
                        share, PendingIntent.FLAG_CANCEL_CURRENT))
                customTabsIntent = builder?.build()
                mActivity?.let { activity ->
                    customTabsIntent?.launchUrl(activity, Uri.parse(url))
                }
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(mContext, browserIntent, null)
            }
        }
    }

    private fun bindCalendarCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Upcoming Events"
        holder.itemView.home_card_subtitle.text = "UNIVERSITY NOTIFICATIONS"
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

        val labs = MainActivity.getLabsInstance()
        labs.room(roomID).subscribe({ room ->
            mActivity.runOnUiThread {
                holder.itemView.home_card_title.text = room.name
                val rooms = arrayListOf(room)
                holder.itemView.home_card_rv.adapter = LaundryRoomAdapter(mContext, rooms, null, true)
            }

        }, { throwable -> mActivity.runOnUiThread { throwable.printStackTrace() } } )
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
        return !(resolveInfos == null || resolveInfos.isEmpty())
    }
}