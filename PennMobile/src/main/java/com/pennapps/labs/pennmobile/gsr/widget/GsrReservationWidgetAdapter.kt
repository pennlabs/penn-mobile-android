package com.pennapps.labs.pennmobile.gsr.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.GsrReservationsRequest
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
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

        override fun onCreate() {
            mGsrReservationsRequest = GsrReservationWidget.gsrReservationsRequestInstance
        }

        /**
         * Runs on a binder thread when the provider or [GsrReservationWidget.requestUpdate] notifies the
         * collection, so loading synchronously here is the documented way to refresh before getViewAt.
         */
        override fun onDataSetChanged() {
            dataSet = loadReservations()
            Log.d("GsrReservationWidgetAdapter", "Loaded ${dataSet.size} reservations for widget $appWidgetId")
        }

        override fun onDestroy() = Unit

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

        /** Logged out clears the list; a failed request keeps what's shown rather than blanking it. */
        private fun loadReservations(): List<GSRReservation> {
            val token = sharedPreferences.getString(context.getString(R.string.access_token), "")
            val request = mGsrReservationsRequest
            if (token.isNullOrEmpty() || request == null) return emptyList()
            return try {
                request
                    .getGsrReservations("Bearer $token")
                    .toBlocking()
                    .first()
                    .sortedBy { it.fromDate }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.w("GsrReservationWidgetAdapter", "Couldn't load reservations", e)
                dataSet
            }
        }
    }
}
