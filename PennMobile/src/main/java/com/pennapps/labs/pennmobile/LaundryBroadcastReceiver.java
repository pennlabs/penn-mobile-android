package com.pennapps.labs.pennmobile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

public class LaundryBroadcastReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 0;
    public LaundryBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LaundryRoom laundryRoom = intent.getParcelableExtra(context.getString(R.string.laundry));
        LaundryMachine machine = intent.getParcelableExtra(context.getString(R.string.laundry_machine_intent));
        if(laundryRoom == null || machine == null){
            return;
        }
        StringBuilder builder = new StringBuilder();
        String roomname = laundryRoom.name.replace("FL","Floor");
        String type = machine.machine_type.contains(context.getString(R.string.laundry_washer_textview)) ?
                context.getString(R.string.laundry_washer_textview) : context.getString(R.string.laundry_dryer_textview);
        builder.append(roomname).append(" ").append(type).append(" ").append(machine.number).append(" is available");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_local_laundry_service)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(builder);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(ContextCompat.getColor(context, R.color.color_primary));
        }
        Intent main = new Intent(context, MainActivity.class);
        main.putExtra(context.getString(R.string.laundry_notification_alarm_intent), true);
        main.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        main.putExtra(context.getString(R.string.laundry_hall_no), laundryRoom.hall_no);
        PendingIntent notifyIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, main, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notifyIntent);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
        PendingIntent fromIntent = PendingIntent.getBroadcast(context, intent.getIntExtra(context.getString(R.string.laundry_position), 0),
                intent, PendingIntent.FLAG_NO_CREATE);
        fromIntent.cancel();
    }
}
