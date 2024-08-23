package com.pennapps.labs.pennmobile

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.adapters.DiningHallWidgetAdapter
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.classes.Venue
import com.squareup.okhttp.OkHttpClient
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.GsonConverter
import java.util.concurrent.TimeUnit

class DiningHallListWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    companion object {
        private var mDiningRequest : DiningRequest? = null;
        val ACTION_AUTO_UPDATE = "AUTO_UPDATE"

        @JvmStatic
        val diningRequestInstance : DiningRequest
            get() {
                if (mDiningRequest == null) {
                    val gsonBuilder = GsonBuilder()

                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Venue?>?>() {}.type,
                        Serializer.VenueSerializer()
                    )

                    val gson = gsonBuilder.create()
                    val okHttpClient = OkHttpClient()
                    okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS)
                    okHttpClient.setReadTimeout(35, TimeUnit.SECONDS)
                    okHttpClient.setWriteTimeout(35, TimeUnit.SECONDS)

                    val restAdapter =
                        RestAdapter.Builder()
                            .setConverter(GsonConverter(gson))
                            .setClient(OkClient(okHttpClient))
                            .setEndpoint("https://pennmobile.org/api")
                            .build()
                    mDiningRequest = restAdapter.create(DiningRequest::class.java)
                }
                return mDiningRequest!!
            }
    }

}