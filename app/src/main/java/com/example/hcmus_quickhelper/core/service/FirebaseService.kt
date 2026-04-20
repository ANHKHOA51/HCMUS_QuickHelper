package com.example.hcmus_quickhelper.core.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.features.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.notification?.title ?: "No title"
        val body = remoteMessage.notification?.body ?: "No body"

        NotificationHelper.showNotification(
            this,
            title,
            body
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

//    fun showNotification(title: String?, body: String?) {
//        val builder = NotificationCompat.Builder(this, "high_channel")
//            .setSmallIcon(R.drawable.ic_launcher_foreground) // đổi icon của bạn
//            .setContentTitle(title)
//            .setContentText(body)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(1, builder.build())
//    }
}