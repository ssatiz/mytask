package com.influx.marcus.theatres.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.payment.PaymentScreen
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.app.NotificationChannel




/**
 * Created by Kavitha on 27-03-2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName
    internal lateinit var notificationManager: NotificationManager
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        try {
            Log.e(TAG, "From: " + remoteMessage!!.getFrom()!!)
            Log.e(TAG, "From: " + remoteMessage.notification?.title)

            val tittle_get = remoteMessage.notification?.title
            val message_get = remoteMessage.notification?.body
//        val image_get = remoteMessage.getData()["image"]

            shownotification(tittle_get!!, message_get!!)
            Log.v("PushNotification", "tittle_get " + tittle_get + "message_get " + message_get)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun shownotification(tittle_get: String, message_get: String) {

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val intent = Intent(this, PaymentScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pattern = longArrayOf(500, 500, 500, 500, 500)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

//        if (image_get != null && !image_get.equals("", ignoreCase = true)) {
//
//            val bitmap = getBitmapFromURL(image_get)
//
//            try {
//
//
//                val notiStyle = NotificationCompat.BigPictureStyle()
//                // notiStyle.setSummaryText(intent.getExtras().getString("message"));
//
//                notiStyle.bigPicture(bitmap)
//                notificationManager = this
//                        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                var contentIntent: PendingIntent? = null
//
//                val gotoIntent = Intent()
//                gotoIntent.setClassName(this, "com.grandcinema.gcapp.screens.screenActivity.SplashscreenActivity")
//                contentIntent = PendingIntent.getActivity(this,
//                        (Math.random() * 100).toInt(), gotoIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT)
//                val mBuilder = android.support.v7.app.NotificationCompat.Builder(
//                        this)
//                val notification: Notification
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setWhen(0)
//                            .setAutoCancel(true)
//                            .setContentTitle(tittle_get)
//                            .setStyle(NotificationCompat.BigTextStyle().bigText(message_get))
//                            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                            .setContentIntent(contentIntent)
//                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//
//                            .setContentText(message_get)
//                            .setStyle(notiStyle).build()
//                } else {
//                    notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setWhen(0)
//                            .setAutoCancel(true)
//                            .setContentTitle(tittle_get)
//                            .setStyle(NotificationCompat.BigTextStyle().bigText(message_get))
//                            .setContentIntent(contentIntent)
//                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//
//                            .setContentText(message_get)
//                            .setStyle(notiStyle).build()
//                }
//
//                notification.flags = Notification.FLAG_AUTO_CANCEL
//                notificationManager.notify(m, notiStyle.build())
//
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//
//        } else {
        val channelId = "some_channel_id"
        val res = this.getResources()
        val notificationBuilder2 = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(tittle_get)
                .setContentText(message_get)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.app_icon))
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message_get))
                .setContentIntent(pendingIntent) as NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder2.setSmallIcon(R.mipmap.app_icon)
            notificationBuilder2.color = ContextCompat.getColor(this, R.color.white)
        } else {
            notificationBuilder2.setSmallIcon(R.mipmap.app_icon)
        }
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(m, notificationBuilder2.build())
//        }

    }

    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            return myBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }
}