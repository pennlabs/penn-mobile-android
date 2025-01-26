package com.pennapps.labs.pennmobile.gsr.widget

import android.app.job.JobParameters
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pennapps.labs.pennmobile.R

class GsrReservationWidgetJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("GsrReservationWidgetJobService", "Job started")

        Thread {
            try {
                val context = applicationContext
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids =
                    appWidgetManager.getAppWidgetIds(
                        ComponentName(context!!, GsrReservationWidget::class.java),
                    )
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    ids,
                    R.id.gsr_reservation_widget_stack_view,
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    appWidgetManager.notifyAppWidgetViewDataChanged(
                        ids,
                        R.id.gsr_reservation_widget_stack_view,
                    )
                }, 5_000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("GsrReservationWidgetJobService", "Job stopped")
        return true
    }
}
