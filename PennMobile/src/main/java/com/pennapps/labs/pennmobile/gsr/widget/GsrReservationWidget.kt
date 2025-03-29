package com.pennapps.labs.pennmobile.gsr.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.GsrReservationsRequest
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GsrReservationWidget : AppWidgetProvider() {
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

            mainActivityIntent.putExtra("Gsr_Tab_Switch", 1)

            val serviceIntent = Intent(context, GsrReservationWidgetAdapter::class.java)
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

            // Setting up the widget remoteViews; change cardview to something else
            val views = RemoteViews(context.packageName, R.layout.gsr_reservation_widget)
            views.setRemoteAdapter(R.id.gsr_reservation_widget_stack_view, serviceIntent)
            views.setEmptyView(
                R.id.gsr_reservation_widget_stack_view,
                R.id.gsr_reservation_widget_empty_view,
            )

            // Setting up the intents for the remoteview for both when it is empty and
            // when it loads the collection view (in this case we use setPendingIntentTemplate to
            // the entire stackView that contains all the items).
            views.setPendingIntentTemplate(R.id.gsr_reservation_widget_stack_view, pendingIntent)
            views.setOnClickPendingIntent(R.id.gsr_reservation_widget_empty_view, pendingIntent)

            // Notify appwidgetviewdata has changed to call getViewAt to set up the widget UI
            // and handle update for every appwidget item in the Collection widget.
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                R.id.gsr_reservation_widget_stack_view,
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(
        context: Context?,
        appWidgetIds: IntArray?,
    ) {
        super.onDeleted(context, appWidgetIds)
    }

    // onEnabled and onDisabled are typically used for alarmManager testing and logs to check whether
    // appwidget is properly enabled/disabled.
//    override fun onEnabled(context: Context) {
//        // start alarm
//        val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
//        appWidgetAlarm.startAlarm()
//    }
//
//    override fun onDisabled(context: Context) {
//        val appWidgetManager = AppWidgetManager.getInstance(context)
//        val componentName = ComponentName(context.packageName, GsrReservationWidget::class.java.name)
//        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
//
//        if (appWidgetIds.isEmpty()) {
//            // stop alarm
//            val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
//            appWidgetAlarm.stopAlarm()
//        }
//    }

//    override fun onReceive(context: Context, intent: Intent) {
//        super.onReceive(context, intent)
//
//        if (intent.action == ACTION_AUTO_UPDATE) {
//            Log.d("intent received", "yay")
//            context.sendBroadcast(Intent(UPDATE_GSR_WIDGET))
//        }
//    }

    companion object {
        private var mGSRReservationsRequest: GsrReservationsRequest? = null
        const val UPDATE_GSR_WIDGET = "com.pennapps.labs.pennmobile.UPDATE_GSR_WIDGET"

        @JvmStatic
        val gsrReservationsRequestInstance: GsrReservationsRequest
            get() {
                if (mGSRReservationsRequest == null) {
                    val gsonBuilder = GsonBuilder()

                    // RegisterTypeAdapter with GsrReservationSerializer
                    // since we are only accessing gsr reservations
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<GSRReservation?>?>() {}.type,
                        Serializer.GsrReservationSerializer(),
                    )

                    val gson = gsonBuilder.create()

                    val okHttpClient =
                        OkHttpClient
                            .Builder()
                            .connectTimeout(35, TimeUnit.SECONDS)
                            .readTimeout(35, TimeUnit.SECONDS)
                            .writeTimeout(35, TimeUnit.SECONDS)
                            .build()

                    val retrofit =
                        Retrofit
                            .Builder()
                            .baseUrl("https://pennmobile.org/api/")
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build()
                    mGSRReservationsRequest = retrofit.create(GsrReservationsRequest::class.java)
                }
                return mGSRReservationsRequest!!
            }
    }
}
