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
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue
import com.squareup.okhttp.OkHttpClient
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.GsonConverter
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
            mainActivityIntent.putExtra("Widget_Tab_Switch", 2)
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
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(DiningHall::class.java,
                        Serializer.MenuSerializer()
                    )
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<Venue?>?>() {}.type,
                        Serializer.VenueSerializer()
                    )

                    val gson = gsonBuilder.create()
                    val okHttpClient = OkHttpClient()
                    okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS) // Connection timeout
                    okHttpClient.setReadTimeout(35, TimeUnit.SECONDS)    // Read timeout
                    okHttpClient.setWriteTimeout(35, TimeUnit.SECONDS)   // Write timeout
                    val restAdapter = RestAdapter.Builder()
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

