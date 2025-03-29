package com.pennapps.labs.pennmobile.gsr.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.GsrReservationsRequest
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget.Companion.UPDATE_GSR_WIDGET
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import rx.Observable
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import java.net.URL

class GsrReservationWidgetAdapter : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = GsrReservationWidgetFactory(applicationContext, intent)

    class GsrReservationWidgetFactory(
        private val context: Context,
        intent: Intent,
    ) : RemoteViewsFactory {
        private var mGsrReservationsRequest: GsrReservationsRequest? = null
        private var appWidgetId: Int =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            )
        private var dataSet: List<GSRReservation> = emptyList()
        private var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        private val gsrReservationWidgetReceiver = GsrReservationWidgetReceiver()

        inner class GsrReservationWidgetReceiver : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                if (intent?.action == UPDATE_GSR_WIDGET) {
                    getWidgetGsrReservations()
                    onDataSetChanged()
                }
            }
        }

        override fun onCreate() {
            // Register BroadcastReceiver
            registerReceiver(
                context,
                gsrReservationWidgetReceiver,
                IntentFilter(UPDATE_GSR_WIDGET),
                ContextCompat.RECEIVER_EXPORTED,
            )

            mGsrReservationsRequest = GsrReservationWidget.gsrReservationsRequestInstance
            getWidgetGsrReservations()
            val appWidgetManager: AppWidgetManager =
                AppWidgetManager.getInstance(context)
            Handler(Looper.getMainLooper()).postDelayed({
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.gsr_reservation_widget_stack_view,
                )
            }, 3000)
        }

        // List size is not updated in time when cancelling reser
        override fun onDataSetChanged() {
            // Log.d("GsrReservationWidgetAdapter", "Sanity Check: List size: ${dataSet.size}")
        }

        override fun onDestroy() {
            context.unregisterReceiver(gsrReservationWidgetReceiver)
        }

        override fun getCount(): Int = dataSet.size

        override fun getViewAt(index: Int): RemoteViews {
            Log.d("gsrreservationwidget", "Size is ${dataSet.size}")
            var i = index
            while (i >= dataSet.size) {
                Log.d("GsrReservationWidgetAdapter", "index out of bounds")
                i -= 1
            }
            Log.d("GsrReservationWidgetAdapter", "List size: ${dataSet.size}, Requested index: $i")
            val reservation = dataSet[i]
            val roomName = reservation.name

            val formatter: DateTimeFormatter =
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            val from = formatter.parseDateTime(reservation.fromDate)
            val to = formatter.parseDateTime(reservation.toDate)
            val day = from.toString("EEEE, MMMM d")
            val fromHour = from.toString("h:mm a")
            val toHour = to.toString("h:mm a")

            val imageUrl =
                reservation.info?.get("thumbnail")
                    ?: "https://s3.us-east-2.amazonaws.com/labs.api/dining/MBA+Cafe.jpg"

            val views = RemoteViews(context.packageName, R.layout.gsr_reservation_widget_item)
            views.setTextViewText(R.id.gsr_reservation_widget_item_location_tv, roomName)
            views.setTextViewText(
                R.id.gsr_reservation_widget_item_time_tv,
                "$day\n$fromHour-$toHour",
            )

            try {
                val urlConnection = URL(imageUrl)
                val connection =
                    urlConnection
                        .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                views.setImageViewBitmap(R.id.gsr_reservation_widget_item_iv, myBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val extras = Bundle()
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            views.setOnClickFillInIntent(R.id.gsr_reservation_widget_item_root, fillInIntent)

            return views
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(id: Int): Long = id.toLong()

        override fun hasStableIds(): Boolean = true

        fun getWidgetGsrReservations() {
            try {
                val token =
                    sharedPreferences.getString(
                        context.getString(R.string.access_token),
                        "",
                    )
                if (mGsrReservationsRequest != null && token != "") {
                    mGsrReservationsRequest!!
                        .getGsrReservations("Bearer $token")
                        .subscribeOn(Schedulers.io()) // Ask Baron what this does
                        .flatMap { reservations -> Observable.from(reservations) }
                        .flatMap { reservation ->
                            Observable.just(reservation)
                        }.toList()
                        .subscribe { reservations ->
                            dataSet = reservations
                            val appWidgetManager: AppWidgetManager =
                                AppWidgetManager.getInstance(context)
                            appWidgetManager.notifyAppWidgetViewDataChanged(
                                appWidgetId,
                                R.id.gsr_reservation_widget_stack_view,
                            )
                        }
                } else if (token == "") {
                    dataSet = mutableListOf()
                    val appWidgetManager: AppWidgetManager =
                        AppWidgetManager.getInstance(context)
                    appWidgetManager.notifyAppWidgetViewDataChanged(
                        appWidgetId,
                        R.id.gsr_reservation_widget_stack_view,
                    )
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }
    }
}