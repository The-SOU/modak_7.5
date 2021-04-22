package com.example.modaktestone.navigation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification!!.title
        val body = remoteMessage.notification!!.body
        val action = remoteMessage.notification!!.clickAction

        val intent = Intent(action)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        showNotification(title!!, body!!, pendingIntent)
    }

    private fun showNotification(title: String, body: String, intent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create notification channel
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("CHANNEL_ID", "channel_name", importance)
            mChannel.description = "description"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

        }

        val mBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title)
            .setContentText(body).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(intent).setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(123, mBuilder.build())
    }
}