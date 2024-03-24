package com.pennapps.labs.pennmobile.adapters

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.DiningFragment
import com.pennapps.labs.pennmobile.DiningHallWidget
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.DiningRequest
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.squareup.picasso.Picasso
import com.pennapps.labs.pennmobile.classes.Venue
import io.reactivex.Observable

class DiningHallWidgetAdapter : RemoteViewsService(){

    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return diningWidgetFactory(applicationContext, p0)
    }
    class diningWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {
        private var mDiningRequest: DiningRequest? = null
        private lateinit var loaded: BooleanArray
        private lateinit var sortBy: String
        private var appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        private var exampleData = arrayOf("Lauder\nCollege House", "1920\nCommons", "Hill\nHouse", "English\nHouse", "Falk\nKosher Dining", "McClelland\nExpress", "Houston\nMarket", "Quaker\nKitchen")
        private var exampleImage = intArrayOf(R.drawable.dining_nch, R.drawable.dining_commons, R.drawable.dining_hill_house, R.drawable.dining_kceh, R.drawable.dining_hillel, R.drawable.dining_mcclelland, R.drawable.dining_houston, R.drawable.dining_quaker)
        private var openCondition = true
        private var dataSet: List<DiningHall> = emptyList()

        // Connection to data source
        override fun onCreate() {
            getWidgetDiningHalls()
            //connect to data source
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget_item)
            mDiningRequest = DiningHallWidget.diningRequestInstance
            loaded = BooleanArray(dataSet.size)
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
        }

        // The place where we fetch data from the source set new data and update collection widget accordingly
        override fun onDataSetChanged() {
            //refresh data -> Update data every 30 minutes
            getWidgetDiningHalls()

        }

        override fun onDestroy() {
            //close connection to data source
        }

        override fun getCount(): Int {
            return dataSet.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            if (position >= getCount()) {
                return loadingView
            }
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget_item)
            views.setTextViewText(R.id.appwidget_text, dataSet[position].name)
            views.setImageViewResource(R.id.diningBackground, dataSet[position].image)
            views.setImageViewResource(R.id.imageView, R.drawable.splash_screen435)
            val extras = Bundle()
            extras.putInt("key_data", position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            views.setOnClickFillInIntent(R.id.widgetBackground, fillInIntent)
            if(openCondition) {
                views.setTextViewText(R.id.textView3, " Open ")
                views.setInt(R.id.textView3, "setBackgroundColor", Color.parseColor("#6DB786"))
                views.setImageViewResource(R.id.imageView, R.drawable.baseline_check_circle_24)
            }
            return views
        }

        override fun getLoadingView(): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
            return views
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
            if (mDiningRequest != null) {
                mDiningRequest!!.venues()
                    .flatMap { venues -> Observable.fromIterable(venues) }
                    .flatMap { venue ->
                        val hall = createHall(venue)
                        Observable.just(hall) }
                    .toList()
                    .subscribe {  diningHalls ->
                        dataSet = diningHalls
                        //Update data set
                        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
                        val views = RemoteViews(context.packageName, R.layout.dining_hall_widget)
                        appWidgetManager.updateAppWidget(appWidgetId,views)
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview)
                        //notifyDataSetChanged
                        //notifyDataSetChanged
            }

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