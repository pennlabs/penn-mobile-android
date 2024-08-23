package com.pennapps.labs.pennmobile.adapters

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.DiningHallWidget
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue
import rx.Observable

// For detailed documentation about app widgets using xml layout, check out this link below:
// https://programmer.group/app-widgets-details-four-remoteviews-remoteviews-service-and-remoteviews-factory.html

class DiningHallWidgetAdapter : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return DiningWidgetFactory(applicationContext, p0)
    }

    // The appwidget RemoteViewsFactory updates the data on the appwidget by:
    // 1. onUpdate automatically called every 30 minutes (in DiningHallWidget class)
    // 2. onUpdate recreates RemoteView and consequentially call onCreate
    // 3. In onCreate, the function getWidgetDiningHalls is called
    // 4. getWidgetDiningHall makes a network request to update diningHall data and calls notifyAppWidgetViewDataChanged, which then calls
    //      getViewAt() and updates the UI of the widget).
    class DiningWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {
        private var mDiningRequest: DiningRequest? = null
        private var appWidgetId: Int =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            )
        private var dataSet: List<DiningHall> = emptyList()

        // Connection to data source
        override fun onCreate() {
            // connect to data source
            mDiningRequest = DiningHallWidget.diningRequestInstance
            getWidgetDiningHalls()
        }

        // This function is called when notifyDataSetChanged() is triggered on the remote adapter.
        // This allows a RemoteViewsFactory to respond to data changes by updating any internal
        // references. However, this process is already performed in the getDiningHalls()
        // function alongside the network request, we do not use this function for this specific
        // widget.
        override fun onDataSetChanged() {
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return dataSet.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget_item)
            views.setTextViewText(R.id.appwidget_text, dataSet[position].name)
            views.setImageViewResource(R.id.diningBackground, dataSet[position].image)

            // Set dining hall open status
            if (dataSet[position].isOpen) {
                views.setImageViewResource(R.id.imageView, R.drawable.baseline_check_circle_24)
                views.setInt(R.id.open_status_label, "setBackgroundColor", Color.parseColor("#6DB786"))

                if (dataSet[position].openMeal() != "all" && dataSet[position].openMeal() != null) {
                    val resources = context.resources
                    val openLabel: String =
                        resources.getString(getOpenStatusLabel(dataSet[position].openMeal() ?: ""))
                    views.setTextViewText(R.id.open_status_label, openLabel)
                }
            } else {
                views.setImageViewResource(R.id.imageView, R.drawable.baseline_cancel_24)
                views.setInt(R.id.open_status_label, "setBackgroundColor", Color.parseColor("#990000"))

                if (dataSet[position].openTimes().isEmpty()) {
                    views.setTextViewText(R.id.open_status_label, "Closed Today")
                } else {
                    views.setTextViewText(R.id.open_status_label, "Closed")
                }
            }

            // For future feature of clicking app widget item would direct to the respective dining
            // hall info page
            val extras = Bundle()
            extras.putInt("dining_hall_id", dataSet[position].id)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            views.setOnClickFillInIntent(R.id.widgetBackground, fillInIntent)

            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        private fun getWidgetDiningHalls() {
            try {
                if (mDiningRequest != null) {
                    mDiningRequest!!.venues()
                        .flatMap { venues -> Observable.from(venues) }
                        .flatMap { venue ->
                            val hall = createHall(venue)
                            Observable.just(hall)
                        }
                        .toList()
                        .subscribe { diningHalls ->
                            dataSet = diningHalls
                            val appWidgetManager: AppWidgetManager =
                                AppWidgetManager.getInstance(context)
                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
                        }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }

        private fun getOpenStatusLabel(openMeal: String): Int {
            return when (openMeal) {
                "Breakfast" -> R.string.dining_hall_breakfast
                "Brunch" -> R.string.dining_hall_brunch
                "Lunch" -> R.string.dining_hall_lunch
                "Dinner" -> R.string.dining_hall_dinner
                "Late Night" -> R.string.dining_hall_late_night
                else -> R.string.dining_hall_open
            }
        }

        companion object {
            fun createHall(venue: Venue): DiningHall {
                when (venue.id) {
                    593 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_commons,
                    )

                    636 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_hill_house,
                    )

                    637 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_kceh,
                    )

                    638 -> return DiningHall(
                        venue.id,
                        "Falk Kosher\nDining",
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_hillel,
                    )

                    639 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_houston,
                    )

                    640 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_marks,
                    )

                    641 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_accenture,
                    )

                    642 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_joes_cafe,
                    )

                    1442 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_nch,
                    )

                    747 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_mcclelland,
                    )

                    1057 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_gourmet_grocer,
                    )

                    1058 -> return DiningHall(
                        venue.id,
                        "Tortas Frontera",
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_tortas,
                    )

                    1163 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_commons,
                    )

                    1731 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_nch,
                    )

                    1732 -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_mba_cafe,
                    )

                    1733 -> return DiningHall(
                        venue.id,
                        "Pret a Manger Locust",
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_pret_a_manger,
                    )

                    else -> return DiningHall(
                        venue.id,
                        venue.name,
                        venue.isResidential,
                        venue.getHours(),
                        venue,
                        R.drawable.dining_commons,
                    )
                }
            }
        }
    }
}
