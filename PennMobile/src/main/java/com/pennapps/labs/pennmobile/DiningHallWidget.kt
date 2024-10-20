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

/**
 * Implementation of App Widget functionality.
 */
class DiningHallWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
                flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            // The value we put in the Widget_Tab_Switch Extra is 1 (originally tried to implement
            // global variable but it involves singleton class), and as long as the
            // value of the Extra is not -1 (getIntExtra declared to have default value -1 in MainActivity)
            // we set the tab as the dining tab (so users could access the dining tab when they
            // click on any of the widget). Extra could be further utilized for clicking
            // on individual items on the widget to access their info in the app (future feature
            // to be implemented).
            mainActivityIntent.putExtra("Widget_Tab_Switch", 1)

            // Set up intent for adapter and pendingIntent to allow users to access the app when
            // clicking on the items on the widget.
            val serviceIntent = Intent(context, DiningHallWidgetAdapter::class.java)
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    mainActivityIntent,
                    PendingIntent.FLAG_IMMUTABLE,
                )
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // setData allows the system to distinguish between different service intents. Without
            // setData, onGetViewFactory is called only once for multiple widgets and send
            // the same intent to all of them.
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))

            // Setting up the widget remoteViews
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
            views.setRemoteAdapter(R.id.stackview, serviceIntent)
            views.setEmptyView(R.id.stackview, R.id.emptyview)

            // Setting up the intents for the remoteview for both when it is empty and
            // when it loads the collection view (in this case we use setPendingIntentTemplate to
            // the entire stackView that contains all the items).
            views.setPendingIntentTemplate(R.id.stackview, pendingIntent)
            views.setOnClickPendingIntent(R.id.emptyview, pendingIntent)

            // Notify appwidgetviewdata has changed to call getViewAt to set up the widget UI
            // and handle update for every appwidget item in the Collection widget.
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    // onEnabled and onDisabled are typically used for alarmManager testing and logs to check whether
    // appwidget is properly enabled/disabled.
    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        private var mDiningRequest: DiningRequest? = null
        val ACTION_AUTO_UPDATE = "AUTO_UPDATE"

        @JvmStatic
        val diningRequestInstance: DiningRequest
            get() {
                if (mDiningRequest == null) {
                    val gsonBuilder = GsonBuilder()

                    // RegisterTypeAdapter with VenueSerializer since we are accessing the
                    // Venue data specifically in our widget.
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Venue?>?>() {}.type,
                        Serializer.VenueSerializer(),
                    )

                    val gson = gsonBuilder.create()
                    val okHttpClient = OkHttpClient()
                    okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS) // Connection timeout
                    okHttpClient.setReadTimeout(35, TimeUnit.SECONDS) // Read timeout
                    okHttpClient.setWriteTimeout(35, TimeUnit.SECONDS) // Write timeout

                    val restAdapter =
                        RestAdapter
                            .Builder()
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
