package com.pennapps.labs.pennmobile

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.adapters.DiningHallWidgetAdapter
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.classes.AppWidgetAlarm
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
        for (appWidgetId in appWidgetIds) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
                flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            mainActivityIntent.putExtra("Widget_Tab_Switch", 2)
            val serviceIntent = Intent(context, DiningHallWidgetAdapter::class.java)
            val pendingIntent : PendingIntent = PendingIntent.getActivity(context, appWidgetId, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
            views.setRemoteAdapter(R.id.stackview, serviceIntent)
            views.setEmptyView(R.id.stackview, R.id.emptyview)
            views.setPendingIntentTemplate(R.id.stackview, pendingIntent)
            views.setOnClickPendingIntent(R.id.emptyview, pendingIntent)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
            appWidgetManager.updateAppWidget(appWidgetId, null)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d("updated_msg", "widget updated")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(ACTION_AUTO_UPDATE))
        {
            Log.d("Updated_msg", "Received!")
            val extras = intent.extras
            val appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, DiningHallWidget::class.java)
            )
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                Log.d("Updated_msg", "Updated!")
                this.onUpdate(context,AppWidgetManager.getInstance(context), appWidgetIds)
            }
            else if (appWidgetIds == null) {
                Log.d("Updated_msg", "null.!")
            }

        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("update_msg", "widget_enabled")
        val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
        appWidgetAlarm.startAlarm()
        if (appWidgetAlarm.alarmUp()) {
            Log.d("update_msg", "Alarm is up")
        }
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d("update_msg", "widget_disabled")
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val diningWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(diningWidgetComponentName)
        if (appWidgetIds.size == 0) {
            val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
            appWidgetAlarm.stopAlarm()
        }
    }

    companion object {
        private var mDiningRequest: DiningRequest? = null
        val ACTION_AUTO_UPDATE = "AUTO_UPDATE"
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


