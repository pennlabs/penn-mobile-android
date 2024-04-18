package com.pennapps.labs.pennmobile.adapters

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.DiningHallWidget
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue
import rx.Observable

// For detailed documentation about app widgets using xml layout, check out this link below:
// https://programmer.group/app-widgets-details-four-remoteviews-remoteviews-service-and-remoteviews-factory.html

class DiningHallWidgetAdapter : RemoteViewsService(){

    // Sole member function for remotesviewservice, used to return the remotesviewfactory that
    // takes in context and intent as argument.
    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return diningWidgetFactory(applicationContext, p0)
    }

    /* The adapter equivalent for remoteviews. Considering that app widgets are in another
    process compared to the main app, we can only use a RemotesViewFactory to connect the data
    to our RemoteViews UI. Inner class for RemotesViewService.
    */
    class diningWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {
        private var mDiningRequest: DiningRequest? = null
        private lateinit var loaded: BooleanArray
        private lateinit var sortBy: String
        private var appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        private var exampleData = arrayOf("Lauder\nCollege House", "1920\nCommons", "Hill\nHouse", "English\nHouse", "Falk\nKosher Dining", "McClelland\nExpress", "Houston\nMarket", "Quaker\nKitchen")
        private var exampleImage = intArrayOf(R.drawable.dining_nch, R.drawable.dining_commons, R.drawable.dining_hill_house, R.drawable.dining_kceh, R.drawable.dining_hillel, R.drawable.dining_mcclelland, R.drawable.dining_houston, R.drawable.dining_quaker)
        private var dataSet: List<DiningHall> = emptyList()

        // Connection to data source
        override fun onCreate() {
            //connect to data source
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget_item)
            mDiningRequest = DiningHallWidget.diningRequestInstance
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            getWidgetDiningHalls()
        }

        // The place where we fetch data from the source set new data and update collection widget accordingly
        override fun onDataSetChanged() {
            //refresh data -> Update data every 30 minutes
            //getWidgetDiningHalls()
        }

        override fun onDestroy() {
            //close connection to data source
        }

        override fun getCount(): Int {
            Log.d("msg", "${dataSet.size}")
            return dataSet.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget_item)
            Log.d("msg", "position: ${position}, name: ${dataSet[position].name}")
            views.setTextViewText(R.id.appwidget_text, dataSet[position].name)
            views.setImageViewResource(R.id.diningBackground, dataSet[position].image)
            if (dataSet[position].isOpen) {
                Log.d("msg", "Open right now")
                views.setImageViewResource(R.id.imageView, R.drawable.baseline_check_circle_24)
                views.setInt(R.id.textView3, "setBackgroundColor", Color.parseColor("#6DB786"))
                if (dataSet[position].openMeal() != "all" && dataSet[position].openMeal() != null) {
                    val resources = context.resources
                    var open_label : String = resources.getString(getOpenStatusLabel(dataSet[position].openMeal() ?: ""))
                    views.setTextViewText(R.id.textView3, open_label)
                }
            }
            else {
                views.setImageViewResource(R.id.imageView, R.drawable.baseline_cancel_24)
                views.setInt(R.id.textView3, "setBackgroundColor", Color.parseColor("#990000"))
                if (dataSet[position].openTimes().isEmpty()) {
                    views.setTextViewText(R.id.textView3, "Closed Today")
                }
                else {
                    views.setTextViewText(R.id.textView3, "Closed")
                }
            }
            val extras = Bundle()
            extras.putInt("key_data", position)
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

        @SuppressLint("CheckResult")
        private fun getWidgetDiningHalls() {
            if (mDiningRequest != null) {
                mDiningRequest!!.venues()
                    .flatMap { venues -> Observable.from(venues) }
                    .flatMap { venue ->
                        val hall = createHall(venue)
                        Observable.just(hall) }
                    .toList()
                    .subscribe {  diningHalls ->
                        dataSet = diningHalls
                        Log.d("msg", "Request sent ${dataSet.size}")
                        //Update data set
                        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
                        //notifyDataSetChanged
                        //notifyDataSetChanged
                }
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
                when (venue.id){
                    593 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
                    636 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_hill_house)
                    637 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_kceh)
                    638 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_hillel)
                    639 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_houston)
                    640 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_marks)
                    641 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_accenture)
                    642 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_joes_cafe)
                    1442 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_nch)
                    747 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_mcclelland)
                    1057 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_gourmet_grocer)
                    1058 -> return DiningHall(venue.id, "Tortas Frontera", venue.isResidential, venue.getHours(), venue, R.drawable.dining_tortas)
                    1163 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
                    1731 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_nch)
                    1732 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_mba_cafe)
                    1733 -> return DiningHall(venue.id, "Pret a Manger Locust", venue.isResidential, venue.getHours(), venue, R.drawable.dining_pret_a_manger)
                    else -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
                }
            }
        }


    }
}