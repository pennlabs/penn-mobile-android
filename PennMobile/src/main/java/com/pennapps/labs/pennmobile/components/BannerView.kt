package com.pennapps.labs.pennmobile.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.utils.Utils.dpToPixel
import retrofit.RestAdapter
import retrofit.android.AndroidLog
import retrofit.converter.GsonConverter
import retrofit.http.GET
import java.util.*
import kotlin.concurrent.fixedRateTimer
import rx.Observable
import java.time.LocalDate
import java.time.Month

class BannerView: RelativeLayout {
    private lateinit var imageView: ImageView
    private var assets: List<Asset> = listOf()
    private var currentAsset: Asset? = null
    private lateinit var timer: Timer

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        imageView = ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        addView(imageView)

        contentDescription = context.getString(R.string.interactive_user_engagement_banner)

        fetchImageUrlsAndStart()

        setOnClickListener {
            try {
                val url = currentAsset?.action.let {
                    Uri.parse(it)
                } ?: return@setOnClickListener

                context.startActivity(Intent(Intent.ACTION_VIEW, url).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (e: Exception) {
                return@setOnClickListener
            }
        }
    }

    private fun fetchImageUrlsAndStart() {
        AssetsService.default.getAssets().subscribe {
            if (it != null) {
                assets = it.assets
                post { startRandomImageDisplay() }
            }
        }
    }

    private fun startRandomImageDisplay() {
        timer = fixedRateTimer("ImageTimer", true, 0L, 3000) {
            if (assets.isNotEmpty()) {
                currentAsset = assets.random()
                post { loadAsset() }
            }
        }
    }

    private fun loadAsset() {
        val asset = currentAsset ?: return

        Glide.with(context)
                .load(asset.image)
                .apply(RequestOptions().override(LayoutParams.MATCH_PARENT, Companion.getHeight(context)))
                .into(imageView)

        contentDescription = asset.text
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer.cancel()
    }

    companion object {
        fun shouldShowBanners(): Boolean {
            val date = LocalDate.now()
            val targetDate = LocalDate.of(2023, Month.APRIL, 3)
            return !date.isAfter(targetDate)
        }

        fun getHeight(context: Context) = dpToPixel(context, 100F)
    }

    interface AssetsService {
        @GET("/assets.json")
        fun getAssets(): Observable<AssetsResponse>

        companion object {
            val default: AssetsService = RestAdapter.Builder()
                    .setConverter(GsonConverter(GsonBuilder().create()))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(AndroidLog("Interactive Banners"))
                    .setEndpoint("https://pennlabs.github.io/platform-sample-assets/")
                    .build()
                    .create(AssetsService::class.java)
        }
    }

    class AssetsResponse {
        @SerializedName("assets")
        @Expose
        var assets: List<Asset> = listOf()
    }

    class Asset {
        @SerializedName("image")
        @Expose
        var image: String = ""

        @SerializedName("text")
        @Expose
        var text: String = ""

        @SerializedName("action")
        @Expose
        var action: String? = null
    }
}