package com.pennapps.labs.pennmobile

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.squareup.picasso.Picasso
import android.widget.RelativeLayout
import android.widget.RemoteViews
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.DiningFragment
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.adapters.DiningHallWidgetAdapter
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.Contact
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.pennapps.labs.pennmobile.classes.GSRLocation
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.classes.Post
import com.pennapps.labs.pennmobile.classes.Venue
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class DiningHallWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            // Appwidgets still uses intent between different layouts within itself (not foreign)
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
                flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val serviceIntent = Intent(context, DiningHallWidgetAdapter::class.java)
            // PendingIntent is for foreign applications like appwidgets and notifs, which is foreign to the main application
            val pendingIntent : PendingIntent = PendingIntent.getActivity(context, appWidgetId, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
            views.setRemoteAdapter(R.id.stackview, serviceIntent)
            views.setEmptyView(R.id.stackview, R.id.emptyview)
            views.setPendingIntentTemplate(R.id.stackview, pendingIntent)
            views.setOnClickPendingIntent(R.id.emptyview, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    companion object {
        const val DINING = 3
        private var mDiningRequest: DiningRequest? = null
        @JvmStatic
        val diningRequestInstance: DiningRequest
            get() {
                if (mDiningRequest == null) {
                    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build()
                    val retrofit = Retrofit.Builder()
                        .client(okHttpClient)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl("https://pennmobile.org/api/")
                        .build()
                    mDiningRequest = retrofit.create(DiningRequest::class.java)
                }
                return mDiningRequest!!
            }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = "Lauder\nCollege House"
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

