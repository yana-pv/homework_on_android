package com.example.recipeapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.example.recipeapp.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.recipeapp.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM_DEBUG", "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            Log.d("FCM_DEBUG", "Data payload: $data")
            val kind = data["kind"]
            val title = data["title"] ?: getString(R.string.app_name)
            val message = data["message"] ?: ""

            sendNotification(kind, title, message)
        } else {
            Log.d("FCM_DEBUG", "Empty data payload")
        }
    }

    private fun sendNotification(kind: String?, title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = when (kind) {
            "promo" -> CHANNEL_PROMO_ID
            "auth" -> CHANNEL_AUTH_ID
            else -> CHANNEL_DEFAULT_ID
        }

        createNotificationChannels(notificationManager)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("kind", kind)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Should be replaced with app icon in production
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        when (kind) {
            "promo" -> {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            }
            "auth" -> {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)
            }
            else -> {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW)
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Promo channel
            val promoChannel = NotificationChannel(
                CHANNEL_PROMO_ID,
                getString(R.string.channel_promo_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_promo_desc)
            }

            // Auth and security channel
            val authChannel = NotificationChannel(
                CHANNEL_AUTH_ID,
                getString(R.string.channel_auth_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_auth_desc)
            }

            // Default channel
            val defaultChannel = NotificationChannel(
                CHANNEL_DEFAULT_ID,
                getString(R.string.channel_default_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_default_desc)
            }

            notificationManager.createNotificationChannel(promoChannel)
            notificationManager.createNotificationChannel(authChannel)
            notificationManager.createNotificationChannel(defaultChannel)
        }
    }

    companion object {
        private const val CHANNEL_PROMO_ID = "promo_channel"
        private const val CHANNEL_AUTH_ID = "auth_channel"
        private const val CHANNEL_DEFAULT_ID = "default_channel"
    }
}
