package com.example.hcmus_quickhelper.core.service

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.hcmus_quickhelper.MainActivity
import com.example.hcmus_quickhelper.features.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_DEBUG", "DATA: ${remoteMessage.data}")

        val title = remoteMessage.data["title"] ?: "No title"
        val body = remoteMessage.data["body"] ?: "No body"

        val screen = remoteMessage.data["screen"]

        val intent = Intent(this, MainActivity::class.java).apply {

            putExtra("screen", screen)

            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        NotificationHelper.showNotification(
            this,
            title,
            body,
            intent
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}