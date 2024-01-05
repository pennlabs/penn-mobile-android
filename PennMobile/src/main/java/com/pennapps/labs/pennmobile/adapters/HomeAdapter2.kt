package com.pennapps.labs.pennmobile.adapters

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.DiningFragment.Companion.getMenus
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.CalendarCell
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.pennapps.labs.pennmobile.classes.DiningCell
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.classes.HomepageDataModel
import com.pennapps.labs.pennmobile.classes.LaundryCell
import com.pennapps.labs.pennmobile.classes.NewsCell
import com.pennapps.labs.pennmobile.classes.PollCell
import com.pennapps.labs.pennmobile.classes.PostCell
import com.pennapps.labs.pennmobile.components.sneaker.Utils.convertToDp
import com.pennapps.labs.pennmobile.utils.Utils
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_base_card.view.*
import kotlinx.android.synthetic.main.home_base_card.view.home_card_rv
import kotlinx.android.synthetic.main.home_base_card.view.home_card_subtitle
import kotlinx.android.synthetic.main.home_base_card.view.home_card_title
import kotlinx.android.synthetic.main.poll_card.view.*
import kotlinx.android.synthetic.main.home_news_card.view.*
import kotlinx.android.synthetic.main.home_post_card.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import rx.Observable


class HomeAdapter2(private val dataModel: HomepageDataModel) :
    RecyclerView.Adapter<HomeAdapter2.ViewHolder>() {

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
        private const val POLL = 9
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
            POLL -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.poll_card, parent, false))
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
        val cell = dataModel.getCell(position)
        when (cell.type) {
            "dining" -> bindDiningCell(holder, cell as DiningCell)
            "calendar" -> bindCalendarCell(holder, cell as CalendarCell)
            "news" -> bindNewsCell(holder, cell as NewsCell)
            "laundry" -> bindLaundryCell(holder, cell as LaundryCell)
            "post" -> bindPostCell(holder, cell as PostCell)
            "poll" -> bindPollCell(holder, cell as PollCell, position)
            else -> Log.i("HomeAdapter", "Unsupported type of data at position $position")
        }
    }

    override fun getItemCount(): Int {
        return dataModel.getSize()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int {
        val cell = dataModel.getCell(position)
        return when (cell.type) {
            "reservations" -> RESERVATIONS
            "dining" -> DINING
            "calendar" -> CALENDAR
            "news" -> NEWS
            "laundry" -> LAUNDRY
            "gsr_booking" -> GSR_BOOKING
            "post" -> POST
            "feature" -> FEATURE
            "poll" -> POLL
            else -> NOT_SUPPORTED
        }
    }

    private fun bindDiningCell(holder: ViewHolder, cell: DiningCell) {
        holder.itemView.home_card_title.text = "Favorites"
        holder.itemView.home_card_subtitle.text = "DINING HALLS"
        holder.itemView.dining_prefs_btn.visibility = View.VISIBLE
        holder.itemView.dining_prefs_btn.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, DiningSettingsFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
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
                    val favoritesIdList: List<Int>? = cell.venues
                    diningHalls.forEach {
                        if (favoritesIdList?.contains(it.id) == true) {
                            favorites.add(it)
                        }
                    }
                    getMenus(favorites)
                    holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                        LinearLayoutManager.VERTICAL, false)
                    holder.itemView.home_card_rv.adapter = DiningCardAdapter(favorites)
                }
            }
    }

    private fun bindNewsCell(holder: ViewHolder, cell: NewsCell) {
        val article = cell.article
        holder.itemView.home_news_title.text = article?.title
        holder.itemView.home_news_subtitle.text = article?.subtitle
        holder.itemView.home_news_timestamp.text = article?.timestamp?.trim()

        Glide.with(mContext).load(article?.imageUrl)
            .fitCenter()
            .centerCrop()
            .into(holder.itemView.home_news_iv)

        /** Adds dynamically generated accent color from the fetched image to the news card */
        var accentColor: Int =  getColor(mContext, R.color.black)
        mActivity.lifecycleScope.launch(Dispatchers.Default) {
            val bitmap = Glide.with(mContext).load(article?.imageUrl).submit().get().toBitmap()

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

                dataModel.notifyNewsBlurLoaded()
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
        holder.itemView.blurView.setupWith(holder.itemView.news_card_container)
            .setFrameClearDrawable(ColorDrawable(getColor(mContext, R.color.white)))
            .setBlurAlgorithm(RenderScriptBlur(mContext))
            .setBlurRadius(25f)
            .setHasFixedTransformationMatrix(true)

        holder.itemView.button.setOnClickListener {

            val url = article?.articleUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(
                mContext,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_popup_exit)
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

    private fun bindCalendarCell(holder: ViewHolder, cell: CalendarCell) {
        val events = cell.events ?: ArrayList()

        var i = events.size - 1
        val eventList: ArrayList<CalendarEvent> = ArrayList()
        while (i >= 0) {
            if (!events[i].name.isNullOrEmpty()) {
                eventList.add(events[i])
            }
            i--
        }
        eventList.reverse()

        holder.itemView.home_card_title.text = "Upcoming Events"
        holder.itemView.home_card_subtitle.text = "UNIVERSITY NOTIFICATIONS"

        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
            LinearLayoutManager.VERTICAL, false)

        holder.itemView.home_card_rv.adapter = UniversityEventAdapter(eventList)
    }

    private fun bindLaundryCell(holder: ViewHolder, cell: LaundryCell) {
        val roomID = cell.roomId
        holder.itemView.home_card_subtitle.text = "LAUNDRY"
        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
            LinearLayoutManager.VERTICAL, false)

        val params : ConstraintLayout.LayoutParams =
            holder.itemView.home_card_rv.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        params.marginStart = 0

        holder.itemView.home_card_rv.layoutParams = params

        mStudentLife.room(roomID).subscribe({ room ->
            mActivity.runOnUiThread {
                holder.itemView.home_card_title.text = room.name
                val rooms = arrayListOf(room)
                holder.itemView.home_card_rv.adapter = LaundryRoomAdapter(mContext, rooms, null, true)
            }

        }, { throwable -> mActivity.runOnUiThread { throwable.printStackTrace() } })
    }

    private fun bindPostCell(holder: ViewHolder, cell: PostCell) {
        val post = cell.post
        holder.itemView.home_post_title.text = post.title
        holder.itemView.home_post_subtitle.text = post.subtitle
        holder.itemView.home_post_source.text = "Penn Labs" //post?.clubCode?.capitalize()
        val time = post.startDate?.substring(5, 7) + " / " +
                post.startDate?.substring(8, 10) + " - " +
                post.expireDate?.substring(5, 7) + " / " +
                post.expireDate?.substring(8, 10)
        holder.itemView.home_post_timestamp.text = time
        Glide.with(mContext).load(post.imageUrl)
            .fitCenter()
            .centerCrop()
            .into(holder.itemView.home_post_iv)
        /** Adds dynamically generated accent color from the fetched image to the news card */
        var accentColor: Int =  getColor(mContext, R.color.black)
        mActivity.lifecycleScope.launch(Dispatchers.Default) {
            val bitmap = Glide.with(mContext).load(post.imageUrl).submit().get().toBitmap()
            // Create palette from bitmap
            fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
            val vibrantSwatch: Palette.Swatch? = createPaletteSync(bitmap).darkVibrantSwatch
            vibrantSwatch?.rgb?.let { accentColor = it }
            mActivity.runOnUiThread {
                // Change all the components to match the accent color palette
                vibrantSwatch?.titleTextColor?.let {
                    holder.itemView.home_post_title.setTextColor(ColorUtils.setAlphaComponent(it, 150))
                    holder.itemView.home_post_subtitle.setTextColor(it)
                    holder.itemView.home_post_timestamp.setTextColor(it)
                    holder.itemView.home_post_source.setTextColor(it)
                }
                val bitmapDrawable = BitmapDrawable(
                    holder.view.resources,
                    bitmap)

                holder.itemView.post_card_container.background = bitmapDrawable
                holder.itemView.postBlurView
                    .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))
                dataModel.notifyPostBlurLoaded()
            }
        }
        /** Sets up blur view on post card */
        holder.itemView.postBlurView.setupWith(holder.itemView.post_card_container)
            .setFrameClearDrawable(ColorDrawable(getColor(mContext, R.color.white)))
            .setBlurAlgorithm(RenderScriptBlur(mContext))
            .setBlurRadius(25f)
            .setHasFixedTransformationMatrix(true)
        /** Post clicking logic if there exists a URL **/
        val url = post?.postUrl ?: return
        holder.itemView.home_post_card.setOnClickListener {
            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(mContext,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_popup_exit)
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

    private fun bindPollCell(holder: ViewHolder, cell: PollCell, position: Int) {
        val poll = cell.poll
        holder.itemView.home_card_title?.text = poll.question
        holder.itemView.home_card_subtitle_2?.text = "${poll.totalVotes} Votes"
        if(poll.clubCode != null) {
            holder.itemView.home_card_subtitle?.text = "POLL FROM ${poll.clubCode}"
        }
        holder.itemView.home_card_rv?.layoutManager = LinearLayoutManager(mContext)
        holder.itemView.home_card_rv?.adapter = PollOptionAdapter(ArrayList(poll.options), poll)
        if(!poll.isVisible) {
            holder.itemView.vote_btn?.setOnClickListener {
                var isSelected = false
                poll.options.forEach { isSelected = isSelected || it.selected }
                if (!isSelected) {
                    Toast.makeText(
                        mActivity,
                        "Need to select an option to vote",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                poll.isVisible = true
                (holder.itemView.home_card_rv?.adapter as PollOptionAdapter).notifyDataSetChanged()
                holder.itemView.vote_btn?.isClickable = false
                notifyItemChanged(position)
                val selectedOptions = ArrayList<Int>()
                poll.options.forEach { if (it.id != null && it.selected) {
                    selectedOptions.add(it.id)
                } }
                val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
                val idHash = Utils.getSha256Hash(deviceID)
                mActivity.mNetworkManager.getAccessToken {
                    val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                    val bearerToken = "Bearer " + sp.getString(mContext.getString(R.string.access_token), " ")

                    mStudentLife.createPollVote(
                        bearerToken,
                        idHash,
                        selectedOptions,
                        object : ResponseCallback() {
                            override fun success(response: Response?) {
                                Log.i("HomeAdapter", "Successfully voted for poll!")
                            }

                            override fun failure(error: RetrofitError?) {
                                Log.e("HomeAdapter", "Error voting for poll", error)
                            }

                        })
                }
            }
        } else {
            holder.itemView.vote_btn?.setTextColor(mContext.resources.getColor(R.color.gray))
            holder.itemView.vote_btn?.setOnClickListener {}
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
