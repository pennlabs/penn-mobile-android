package com.pennapps.labs.pennmobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class LaundryBroadcastReceiver extends BroadcastReceiver {

    private int NOTIFICATION_ID;

    public LaundryBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String roomName = intent.getStringExtra(context.getResources().getString(R.string.laundry_room_name));
        String machineType = intent.getStringExtra(context.getResources().getString(R.string.laundry_machine_type));
        int id = intent.getIntExtra(context.getResources().getString(R.string.laundry_machine_id), -1);

        // checks for errors
        if (roomName == null || machineType == null || id == -1) {
            return;
        }

        NOTIFICATION_ID = id + 1;

        StringBuilder builder = new StringBuilder();
        builder.append("A ").append(machineType).append(" in ").append(roomName).append(" is available!");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // build notification
        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Laundry Alarm";
            String channelId = "pennmobile_laundry_alarm";
            String description = "Alarm for laundry machine availability";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(ContextCompat.getColor(context, R.color.color_primary));
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(context, channel.getId())
                    .setSmallIcon(R.drawable.ic_local_laundry_service)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(builder);
        } else {
            String channelId = "pennmobile_laundry_alarm";
            mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_local_laundry_service)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(builder);
        }
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(ContextCompat.getColor(context, R.color.color_primary));
        }

        // intent to go to laundry activity
        Intent laundryIntent = new Intent(context, LaundryActivity.class);
        laundryIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notifyIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, laundryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notifyIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        // cancel intent after notification/alarm goes off
        PendingIntent fromIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        fromIntent.cancel();
    }
}