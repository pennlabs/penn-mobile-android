package com.pennapps.labs.pennmobile.classes

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessaging() : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("TAG", "This is the token: $token")

    }

}


//    private fun sendRegistrationToServer(token: String){
//        println(token)
//    }
//
//    override fun onMessageReceived(msg: RemoteMessage) {
//        super.onMessageReceived(msg)
//        Log.i(TAG, "Message : $msg")
//        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "getInstanceId failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//        val token = task.result?.token
//
//        Log.d(TAG, token)
//        Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//        })
//    }
//
//    private fun sendNotification(body : String){
//        val intent = Intent(this, DiningHall::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        val defSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notifBuilder = NotificationCompat.Builder(this, "sup")
//                .setContentTitle("Notification")
//                .setContentText("Hello world")
//                .setAutoCancel(true)
//                .setSound(defSound)
//                .setContentIntent(pendingIntent)
//
//        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.
//        notify(0, notifBuilder.build())
//
//
//        /*
//        Intent intent = new Intent(this, BackupFragment.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_teamwin_splashlogo)
//                .setContentTitle("FCM Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//         */
//   }