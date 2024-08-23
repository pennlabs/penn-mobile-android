package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue

class DiningHallListWidgetAdapter : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        TODO("Not yet implemented")
    }

    class DiningWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {
        override fun onCreate() {
            TODO("Not yet implemented")
        }

        override fun onDataSetChanged() {
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            TODO("Not yet implemented")
        }

        override fun getViewAt(p0: Int): RemoteViews {
            TODO("Not yet implemented")
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