package com.pennapps.labs.pennmobile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class LaundryBroadcastReceiver extends BroadcastReceiver {
    public LaundryBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_local_laundry_service)
                .setContentTitle("Just trying")
                .setContentText("text text text");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(ContextCompat.getColor(context, R.color.color_primary));
        }
        Intent main = new Intent(context, MainActivity.class);
        main.putExtra(context.getString(R.string.laundry_notification_alarm_intent), true);
        main.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        main.putExtra(context.getString(R.string.laundry_hall_no), intent.getIntExtra(context.getString(R.string.laundry_hall_no), -1));
        PendingIntent notifyIntent = PendingIntent.getActivity(context, 0, main, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(notifyIntent);
        manager.notify(2, mBuilder.build());
        PendingIntent fromIntent = PendingIntent.getBroadcast(context, intent.getIntExtra(context.getString(R.string.laundry_position), 0),
                intent, PendingIntent.FLAG_NO_CREATE);
        fromIntent.cancel();
    }
}
