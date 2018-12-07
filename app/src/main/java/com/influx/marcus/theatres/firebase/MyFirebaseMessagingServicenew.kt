package com.influx.marcus.theatres.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.payment.PaymentScreen


/**
 * Created by Kavitha on 27-03-2018.
 */
class MyFirebaseMessagingServicenew : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("TAG", "From: " + remoteMessage?.from)

        val tittle_get = remoteMessage!!.getData()["title"]
        val message_get = remoteMessage.getData()["body"]
//        startActivity(Intent(this, PaymentScreen::class.java))
        val res = this.getResources()
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentTitle(tittle_get)
                .setContentText(message_get).setAutoCancel(true)

//// Creates an explicit intent for an Activity in your app
//        val resultIntent = Intent(this, PaymentScreen::class.java)
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        val stackBuilder = TaskStackBuilder.create(this)
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(PaymentScreen::class.java)
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent)
//        val resultPendingIntent = stackBuilder.getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val notificationIntent = Intent(this, PaymentScreen::class.java)
//        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationIntent = Intent(this, PaymentScreen::class.java)
        val contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        builder.setContentIntent(contentIntent)


//        builder.setContentIntent(resultPendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())

    }
}