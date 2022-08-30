package com.example.firestore

import activities.PushNotificationFirebase
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val channelName = "com.example.firestore"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
      if(remoteMessage.notification!=null){
          generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
      }
    }
    @SuppressLint("RemoteViewLayout")
    private fun getRemotView(title: String, message: String): RemoteViews {
        val remotView = RemoteViews("com.example.firestore", R.layout.notification)
        remotView.setTextViewText(R.id.tvTitle, title)
        remotView.setTextViewText(R.id.tvMessage, message)
        remotView.setImageViewResource(R.id.ivSwiggy,R.drawable.ic_swiggy)

        return remotView

    }

    private fun generateNotification(title: String, message: String) {

        val intent = Intent(this@MyFirebaseMessagingService, PushNotificationFirebase::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        ).setSmallIcon(R.drawable.ic_swiggy)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemotView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())


    }


}