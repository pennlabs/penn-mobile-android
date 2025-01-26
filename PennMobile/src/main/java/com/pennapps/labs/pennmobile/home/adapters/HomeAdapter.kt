package com.pennapps.labs.pennmobile.home.adapters

import StudentLife
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.components.sneaker.Utils.convertToDp
import com.pennapps.labs.pennmobile.databinding.HomeBaseCardBinding
import com.pennapps.labs.pennmobile.databinding.HomeGsrCardBinding
import com.pennapps.labs.pennmobile.databinding.HomeNewsCardBinding
import com.pennapps.labs.pennmobile.databinding.HomePostCardBinding
import com.pennapps.labs.pennmobile.databinding.PollCardBinding
import com.pennapps.labs.pennmobile.dining.adapters.DiningCardAdapter
import com.pennapps.labs.pennmobile.dining.classes.DiningCell
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.fragments.DiningFragment
import com.pennapps.labs.pennmobile.dining.fragments.DiningFragment.Companion.getMenus
import com.pennapps.labs.pennmobile.dining.fragments.DiningSettingsFragment
import com.pennapps.labs.pennmobile.gsr.classes.GSRCell
import com.pennapps.labs.pennmobile.gsr.fragments.GsrTabbedFragment
import com.pennapps.labs.pennmobile.home.classes.CalendarCell
import com.pennapps.labs.pennmobile.home.classes.CalendarEvent
import com.pennapps.labs.pennmobile.home.classes.HomepageDataModel
import com.pennapps.labs.pennmobile.home.classes.NewsCell
import com.pennapps.labs.pennmobile.home.classes.Poll
import com.pennapps.labs.pennmobile.home.classes.PollCell
import com.pennapps.labs.pennmobile.home.classes.Post
import com.pennapps.labs.pennmobile.home.classes.PostCell
import com.pennapps.labs.pennmobile.home.fragments.NewsFragment
import com.pennapps.labs.pennmobile.home.viewholders.HomeBaseHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomeCalendarHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomeDiningHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomeGSRHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomeLaundryHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomeNewsCardHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomePollHolder
import com.pennapps.labs.pennmobile.home.viewholders.HomePostHolder
import com.pennapps.labs.pennmobile.laundry.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.laundry.classes.LaundryCell
import com.pennapps.labs.pennmobile.utils.Utils
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rx.Observable
import rx.schedulers.Schedulers

class HomeAdapter(
    private val dataModel: HomepageDataModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        private const val DRAFT_NOTE = " (NOTE: THIS IS A DRAFT THAT USERS CANNOT SEE)"
        private const val DRAFT_COLOR = "#ffb300"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        mContext = parent.context
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = mContext as MainActivity

        return when (viewType) {
            DINING -> {
                val itemBinding = HomeBaseCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeDiningHolder(itemBinding)
            }

            CALENDAR -> {
                val itemBinding = HomeBaseCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeCalendarHolder(itemBinding)
            }

            LAUNDRY -> {
                val itemBinding = HomeBaseCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeLaundryHolder(itemBinding)
            }

            NEWS -> {
                val itemBinding = HomeNewsCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeNewsCardHolder(itemBinding)
            }

            POST -> {
                val itemBinding = HomePostCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomePostHolder(itemBinding)
            }

            FEATURE -> {
                val itemBinding = HomePostCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomePostHolder(itemBinding)
            }

            POLL -> {
                val itemBinding = PollCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomePollHolder(itemBinding)
            }

            GSR_BOOKING -> {
                val itemBinding = HomeGsrCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeGSRHolder(itemBinding)
            }

            NOT_SUPPORTED -> {
                ViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false),
                )
            }

            else -> {
                val itemBinding = HomeBaseCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                return HomeBaseHolder(itemBinding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val cell = dataModel.getCell(position)
        when (cell.type) {
            "dining" -> bindDiningCell(holder as HomeDiningHolder, cell as DiningCell)
            "calendar" -> bindCalendarCell(holder as HomeCalendarHolder, cell as CalendarCell)
            "news" -> bindNewsCard(holder as HomeNewsCardHolder, cell as NewsCell)
            "laundry" -> bindLaundryCell(holder as HomeLaundryHolder, cell as LaundryCell)
            "post" -> bindPostCell(holder as HomePostHolder, cell as PostCell)
            "poll" -> bindPollCell(holder as HomePollHolder, position, cell as PollCell)
            "gsr_booking" -> bindGSRCell(holder as HomeGSRHolder, cell as GSRCell)
            "none" -> Log.i("HomeAdapter", "Empty cell at position $position")
            else -> Log.i("HomeAdapter", "Unsupported type of data at position $position")
        }
    }

    override fun getItemCount(): Int = dataModel.getSize()

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

    /** Checks if the chrome tab is supported on the current device. */
    private fun Context.isChromeCustomTabsSupported(): Boolean {
        val serviceIntent = Intent("android.support.customtabs.action.CustomTabsService")
        serviceIntent.setPackage("com.android.chrome")
        val resolveInfos = this.packageManager.queryIntentServices(serviceIntent, 0)
        return resolveInfos.isNotEmpty()
    }

    // Chrome custom tabs to launch news site
    internal inner class NewsCustomTabsServiceConnection : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            name: ComponentName,
            client: CustomTabsClient,
        ) {
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

    private fun bindLaundryCell(
        holder: HomeLaundryHolder,
        cell: LaundryCell,
    ) {
        val roomID = cell.roomId
        holder.homeSubtitle.text = "LAUNDRY"
        holder.homeRv.layoutManager =
            LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false,
            )

        val params: ConstraintLayout.LayoutParams =
            holder.homeRv.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        params.marginStart = 0

        holder.homeRv.layoutParams = params

        try {
            mStudentLife.roomObservable(roomID).subscribeOn(Schedulers.io()).subscribe({ room ->
                mActivity.runOnUiThread {
                    holder.homeTitle.text = room?.name ?: ""
                    val rooms = room?.let { arrayListOf(it) } ?: arrayListOf()
                    holder.homeRv.adapter =
                        LaundryRoomAdapter(
                            mContext,
                            rooms,
                            null,
                            true,
                        )
                }
            }, { throwable -> mActivity.runOnUiThread { throwable.printStackTrace() } })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindDiningCell(
        holder: HomeDiningHolder,
        cell: DiningCell,
    ) {
        holder.homeTitle.text = "Favorites"
        holder.homeSubtitle.text = "DINING HALLS"
        holder.diningPrefsBtn.visibility = View.VISIBLE
        holder.diningPrefsBtn.setOnClickListener {
            mActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, DiningSettingsFragment(dataModel))
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
        try {
            mStudentLife
                .venues()
                .subscribeOn(Schedulers.io())
                .flatMap { venues -> Observable.from(venues) }
                .flatMap { venue ->
                    venue?.let {
                        val hall = DiningFragment.createHall(venue)
                        Observable.just(hall)
                    } ?: Observable.empty()
                }.toList()
                .subscribe { diningHalls ->
                    mActivity.runOnUiThread {
                        val favorites: ArrayList<DiningHall> = arrayListOf()
                        val favoritesIdList: List<Int> = cell.venues
                        diningHalls.forEach {
                            if (favoritesIdList.contains(it.id)) {
                                favorites.add(it)
                            }
                        }
                        getMenus(favorites)
                        holder.homeRv.layoutManager =
                            LinearLayoutManager(
                                mContext,
                                LinearLayoutManager.VERTICAL,
                                false,
                            )
                        holder.homeRv.adapter = DiningCardAdapter(favorites)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindCalendarCell(
        holder: HomeCalendarHolder,
        cell: CalendarCell,
    ) {
        val events = cell.events

        var i = events.size - 1
        val eventList: ArrayList<CalendarEvent> = ArrayList()
        while (i >= 0) {
            if (!events[i].name.isNullOrEmpty()) {
                eventList.add(events[i])
            }
            i--
        }
        eventList.reverse()

        holder.homeTitle.text = "Upcoming Events"
        holder.homeSubtitle.text = "UNIVERSITY NOTIFICATIONS"

        holder.homeRv.layoutManager =
            LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false,
            )

        holder.homeRv.adapter = UniversityEventAdapter(eventList)
    }

    private fun bindPostCell(
        holder: HomePostHolder,
        cell: PostCell,
    ) {
        val post = cell.post

        // if the post is a draft, then change the color and add a note
        if (cell.post.status == Post.DRAFT) {
            holder.homePostTitle.setTextColor(Color.parseColor(DRAFT_COLOR))

            val draftSubtitle = post.subtitle + DRAFT_NOTE
            holder.homePostSubtitle.text = draftSubtitle
        } else {
            holder.homePostSubtitle.text = post.subtitle
        }

        holder.homePostTitle.text = post.title
        holder.homePostSource.text = "Penn Labs" // post?.clubCode?.capitalize()
        val time =
            post.startDate?.substring(5, 7) + " / " +
                post.startDate?.substring(8, 10) + " - " +
                post.expireDate?.substring(5, 7) + " / " +
                post.expireDate?.substring(8, 10)
        holder.homePostTimestamp.text = time
        Glide
            .with(mContext)
            .load(post.imageUrl)
            .fitCenter()
            .centerCrop()
            .into(holder.homePostIv)
        /** Adds dynamically generated accent color from the fetched image to the news card */
        var accentColor: Int = getColor(mContext, R.color.black)
        mActivity.lifecycleScope.launch(Dispatchers.Default) {
            val bitmap =
                withContext(Dispatchers.IO) {
                    Glide
                        .with(mContext)
                        .load(post.imageUrl)
                        .submit()
                        .get()
                }
                    .toBitmap()

            // Create palette from bitmap
            fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
            val vibrantSwatch: Palette.Swatch? = createPaletteSync(bitmap).darkVibrantSwatch
            vibrantSwatch?.rgb?.let { accentColor = it }
            mActivity.runOnUiThread {
                // Change all the components to match the accent color palette
                vibrantSwatch?.titleTextColor?.let {
                    if (cell.post.status != Post.DRAFT) {
                        holder.homePostTitle.setTextColor(
                            ColorUtils.setAlphaComponent(
                                it,
                                150,
                            ),
                        )
                    }
                    holder.homePostSubtitle.setTextColor(it)
                    holder.homePostTimestamp.setTextColor(it)
                    holder.homePostSource.setTextColor(it)
                }
                val bitmapDrawable =
                    BitmapDrawable(
                        holder.itemBinding.root.resources,
                        bitmap,
                    )

                holder.homePostContainer.background = bitmapDrawable
                holder.postBlurView
                    .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))

                // tell dataModel that hte post blur view is done loading
                dataModel.notifyPostBlurLoaded()
            }
        }
        /** Sets up blur view on post card */
        holder.postBlurView
            .setupWith(holder.homePostContainer, RenderScriptBlur(mContext))
            .setFrameClearDrawable(ColorDrawable(getColor(mContext, R.color.white)))
            .setBlurRadius(25f)
        /** Post clicking logic if there exists a URL **/
        val url = post.postUrl ?: return
        holder.homePostCard.setOnClickListener {
            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(
                mContext,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_popup_exit,
            )
            CustomTabsClient.bindCustomTabsService(
                mContext,
                NewsFragment.CUSTOM_TAB_PACKAGE_NAME,
                connection,
            )

            if (mContext.isChromeCustomTabsSupported()) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem(
                    "Share",
                    PendingIntent.getActivity(
                        mContext,
                        0,
                        share,
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                customTabsIntent = builder?.build()
                customTabsIntent?.launchUrl(mActivity, Uri.parse(url))
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(mContext, browserIntent, null)
            }
        }
    }

    private fun bindPollCell(
        holder: HomePollHolder,
        position: Int,
        cell: PollCell,
    ) {
        val poll = cell.poll

        // if the post is a draft, then change the color and add a note
        if (poll.status == Poll.DRAFT) {
            holder.pollTitle.setTextColor(Color.parseColor(DRAFT_COLOR))
            val draftQuestion = poll.question + DRAFT_NOTE
            holder.pollTitle.text = draftQuestion
        } else {
            holder.pollTitle.text = poll.question
        }

        holder.pollSubtitle2.text = "${poll.totalVotes} Votes"
        if (poll.clubCode != null) {
            holder.pollSubtitle.text = "POLL FROM ${poll.clubCode}"
        }
        holder.homeCardRv.layoutManager = LinearLayoutManager(mContext)
        holder.homeCardRv.adapter = PollOptionAdapter(ArrayList(poll.options), poll)
        if (!poll.isVisible) {
            holder.voteBtn.setOnClickListener {
                var isSelected = false
                poll.options.forEach { isSelected = isSelected || it.selected }
                if (!isSelected) {
                    Toast
                        .makeText(
                            mActivity,
                            "Need to select an option to vote",
                            Toast.LENGTH_SHORT,
                        ).show()
                    return@setOnClickListener
                }
                poll.isVisible = true
                (holder.homeCardRv.adapter as PollOptionAdapter).notifyDataSetChanged()
                holder.voteBtn.isClickable = false
                notifyItemChanged(position)
                val selectedOptions = ArrayList<Int>()
                poll.options.forEach {
                    if (it.id != null && it.selected) {
                        selectedOptions.add(it.id)
                    }
                }
                val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
                val idHash = Utils.getSha256Hash(deviceID)
                mActivity.mNetworkManager.getAccessToken {
                    val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                    val bearerToken =
                        "Bearer " + sp.getString(mContext.getString(R.string.access_token), " ")

                    (mContext as MainActivity).lifecycleScope.launch {
                        try {
                            val response = mStudentLife.createPollVote(
                                bearerToken,
                                idHash,
                                selectedOptions,
                            )

                            if (response.isSuccessful) {
                                Log.i("HomeAdapter", "Successfully voted for poll!")
                            } else {
                                val error = Exception(response.errorBody()?.string()
                                    ?: "Unknown Error"
                                )
                                Log.e("HomeAdapter", "Error voting for poll", error)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            holder.voteBtn.setTextColor(mContext.resources.getColor(R.color.gray))
            holder.voteBtn.setOnClickListener {}
        }
    }

    private fun bindGSRCell(
        holder: HomeGSRHolder,
        cell: GSRCell,
    ) {
        holder.homeGSRTitle.text = "Reservations"
        holder.homeGSRSubtitle.text = "Group Study Rooms"
        val reservations = cell.reservations
        holder.homeGSRRv.layoutManager =
            LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false,
            )
        holder.homeGSRButton.text = "Book a Room"
        holder.homeGSRButton.setOnClickListener {
            mActivity.setTab(MainActivity.GSR_ID)
            for (fragment in mActivity.supportFragmentManager.fragments) {
                if (fragment is GsrTabbedFragment) {
                    fragment.viewPager.currentItem = 0
                }
            }
        }
        holder.homeGSRRv.adapter = HomeGsrReservationAdapter(reservations)
    }

    private fun bindNewsCard(
        holder: HomeNewsCardHolder,
        cell: NewsCell,
    ) {
        val article = cell.article
        holder.homeNewsTitle.text = article.title
        holder.homeNewsSubtitle.text = article.subtitle
        holder.homeNewsTimestamp.text = article.timestamp?.trim()

        Glide
            .with(mContext)
            .load(article.imageUrl)
            .fitCenter()
            .centerCrop()
            .into(holder.homeNewsImageView)

        /** Adds dynamically generated accent color from the fetched image to the news card */
        var accentColor: Int = getColor(mContext, R.color.black)
        mActivity.lifecycleScope.launch(Dispatchers.Default) {
            val bitmap =
                withContext(Dispatchers.IO) {
                    Glide
                        .with(mContext)
                        .load(article.imageUrl)
                        .submit()
                        .get()
                }
                    .toBitmap()

            // Create palette from bitmap
            fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
            val vibrantSwatch: Palette.Swatch? = createPaletteSync(bitmap).darkVibrantSwatch
            vibrantSwatch?.rgb?.let { accentColor = it }

            mActivity.runOnUiThread {
                // Change all the components to match the accent color palette
                vibrantSwatch?.titleTextColor?.let {
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.newsCardLogo.drawable),
                        ColorUtils.setAlphaComponent(it, 150),
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.newsInfoIcon.drawable),
                        it,
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.dotDivider.drawable),
                        it,
                    )
                    holder.newsButton.setTextColor(ColorUtils.setAlphaComponent(it, 150))
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.newsButton.background),
                        it,
                    )
                    holder.homeNewsTitle.setTextColor(
                        ColorUtils.setAlphaComponent(
                            it,
                            150,
                        ),
                    )
                    holder.homeNewsSubtitle.setTextColor(it)
                    holder.homeNewsTimestamp.setTextColor(it)
                }
                holder.newsCardContainer.background =
                    BitmapDrawable(
                        holder.itemBinding.root.resources,
                        bitmap,
                    )
                holder.newsBlurView
                    .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))

                // tell model that the news blur view has been loaded
                dataModel.notifyNewsBlurLoaded()
            }
        }

        /** Logic for the more info button on the news card */
        holder.newsInfoIcon.setOnClickListener {
            when (holder.homeNewsSubtitle.visibility) {
                View.GONE -> {
                    holder.homeNewsSubtitle.visibility = View.VISIBLE
                    holder.homeNewsTitle.setPadding(0, 0, 0, 0)
                    holder.newsBlurView
                        .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 250))
                }

                View.VISIBLE -> {
                    holder.homeNewsSubtitle.visibility = View.GONE
                    holder.homeNewsTitle.setPadding(0, 0, 0, convertToDp(mContext, 8f))
                    holder.newsBlurView
                        .setOverlayColor(ColorUtils.setAlphaComponent(accentColor, 150))
                }
            }
        }

        /** Sets up blur view on news card */
        holder.newsBlurView
            .setupWith(holder.newsCardContainer, RenderScriptBlur(mContext))
            .setFrameClearDrawable(ColorDrawable(getColor(mContext, R.color.white)))
            .setBlurRadius(25f)

        holder.newsButton.setOnClickListener {
            val url = article?.articleUrl

            val connection = NewsCustomTabsServiceConnection()
            builder = CustomTabsIntent.Builder()
            share = Intent(Intent.ACTION_SEND)
            share?.type = "text/plain"
            builder?.setToolbarColor(0x3E50B4)
            builder?.setStartAnimations(
                mContext,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_popup_exit,
            )
            CustomTabsClient.bindCustomTabsService(
                mContext,
                NewsFragment.CUSTOM_TAB_PACKAGE_NAME,
                connection,
            )

            if (mContext.isChromeCustomTabsSupported()) {
                share?.putExtra(Intent.EXTRA_TEXT, url)
                builder?.addMenuItem(
                    "Share",
                    PendingIntent.getActivity(
                        mContext,
                        0,
                        share,
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                customTabsIntent = builder?.build()
                customTabsIntent?.launchUrl(mActivity, Uri.parse(url))
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(mContext, browserIntent, null)
            }
        }
    }

    inner class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
