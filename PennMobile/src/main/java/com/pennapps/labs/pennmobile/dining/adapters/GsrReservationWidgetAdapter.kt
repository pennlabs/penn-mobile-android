package com.pennapps.labs.pennmobile.adapters

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.GsrReservationWidget
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.net.HttpURLConnection
import java.net.URL


class GsrReservationWidgetAdapter : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return GsrReservationWidgetFactory(applicationContext, intent)
    }

    class GsrReservationWidgetFactory(
        private val context: Context,
        intent: Intent
    ) : RemoteViewsFactory {
        // Need to change later to actual source
        private var mGsrReservation: GSRReservation? = null
        private var appWidgetId: Int =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            )
        private var dataSet: List<GSRReservation> = emptyList()

        override fun onCreate() {
            mGsrReservation = GsrReservationWidget.gsrReservationInstance
            getWidgetGsrReservations()
        }

        // Only when there's a change to the user's reservations
        override fun onDataSetChanged() {
            getWidgetGsrReservations();
        }

        override fun onDestroy() {

        }

        override fun getCount(): Int {
            return dataSet.size
        }

        // TODO("Set the image, get building name, and hopefully support click behavior")
        override fun getViewAt(index: Int): RemoteViews {
            val reservation = dataSet[index]
            val roomName = reservation.name

            val formatter: DateTimeFormatter =
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            val from = formatter.parseDateTime(reservation.fromDate)
            val to = formatter.parseDateTime(reservation.toDate)
            val day = from.toString("EEEE, MMMM d")
            val fromHour = from.toString("h:mm a")
            val toHour = to.toString("h:mm a")

            val imageUrl = reservation.info?.get("thumbnail") ?:
            "https://s3.us-east-2.amazonaws.com/labs.api/dining/MBA+Cafe.jpg"

            val views = RemoteViews(context.packageName, R.layout.gsr_reservation_widget_item)
            views.setTextViewText(R.id.gsr_reservation_widget_item_location_tv, roomName)
            views.setTextViewText(
                R.id.gsr_reservation_widget_item_time_tv, "$day\n$fromHour-$toHour")
//            Picasso
//                .get()
//                .load(imageUrl)
//                .fit()
//                .centerCrop()
//                .into()
            try {
                val urlConnection = URL(imageUrl)
                val connection = urlConnection
                    .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                views.setImageViewBitmap(R.id.gsr_reservation_widget_item_iv, myBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(id: Int): Long {
            return id.toLong();
        }

        override fun hasStableIds(): Boolean {
            return true;
        }

        // TODO("Actually get the user's reservations")
        private fun getWidgetGsrReservations() {
            try {
                if (mGsrReservation != null) {
                    dataSet = listOf(mGsrReservation!!)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }
    }
}