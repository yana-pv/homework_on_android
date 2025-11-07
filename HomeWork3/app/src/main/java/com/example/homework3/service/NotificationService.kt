package com.example.homework3.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.homework3.MainActivity
import com.example.homework3.R
import com.example.homework3.model.NotificationData
import com.example.homework3.model.NotificationPriority
import com.example.homework3.receiver.ReplyReceiver
import com.example.homework3.utils.Keys

class NotificationService(private val context: Context) {
    private var notificationCounter = Keys.Notification.DEFAULT_NOTIFICATION_ID

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel = NotificationChannel(
                Keys.Notification.CHANNEL_DEFAULT,
                context.getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_default_desc)
            }

            val highPriorityChannel = NotificationChannel(
                Keys.Notification.CHANNEL_HIGH_PRIORITY,
                context.getString(R.string.notification_channel_high_priority),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_high_priority_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(defaultChannel, highPriorityChannel))
        }
    }

    private fun getUniqueNotificationId(): Int {
        return notificationCounter++
    }

    fun showNotification(notificationData: NotificationData) {
        val notificationId = getUniqueNotificationId()

        val channelId = when (notificationData.priority) {
            NotificationPriority.HIGH -> Keys.Notification.CHANNEL_HIGH_PRIORITY
            else -> Keys.Notification.CHANNEL_DEFAULT
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, channelId)
        }
        else {
            NotificationCompat.Builder(context)
        }

        val priority = getPriorityCompat(notificationData.priority)

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.message)
            .setPriority(priority)
            .setAutoCancel(true)

        if (notificationData.priority == NotificationPriority.HIGH) {
            builder.setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        if (notificationData.isExpandable && notificationData.message.isNotEmpty()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.message))
        }

        if (notificationData.shouldOpenApp) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(Keys.Intent.NOTIFICATION_TITLE, notificationData.title)
                putExtra(Keys.Intent.NOTIFICATION_MESSAGE, notificationData.message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                pendingIntentFlags
            )
            builder.setContentIntent(pendingIntent)
        }

        if (notificationData.hasReplyAction) {
            val remoteInput = RemoteInput.Builder(Keys.Actions.REPLY_ACTION_KEY)
                .setLabel(context.getString(R.string.type_your_reply))
                .build()

            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
                putExtra(Keys.Intent.NOTIFICATION_ID, notificationId)
                action = Keys.Actions.REPLY_ACTION
            }

            val replyPendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                replyIntent,
                replyPendingIntentFlags
            )

            val action = NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_edit,
                context.getString(R.string.reply),
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun updateNotification(notificationId: Int, newMessage: String): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val builder = NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(context.getString(R.string.updated_notification))
                .setContentText(newMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText(newMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(notificationId, builder.build())
            return true
        }

        val activeNotifications = notificationManager.activeNotifications
        val targetNotification = activeNotifications.find { it.id == notificationId }

        return if (targetNotification != null) {
            val oldNotification = targetNotification.notification

            val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                oldNotification.channelId ?: Keys.Notification.CHANNEL_DEFAULT
            }
            else {
                Keys.Notification.CHANNEL_DEFAULT
            }

            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(context, channelId)
            }
            else {
                NotificationCompat.Builder(context)
            }

            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(oldNotification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
                .setContentText(newMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText(newMessage))
                .setPriority(oldNotification.priority)

            notificationManager.notify(notificationId, builder.build())
            true
        }
        else {
            false
        }
    }

    fun dismissAllNotifications() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun hasActiveNotifications(): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications.isNotEmpty()
        }
        else {
            true
        }
    }

    private fun getPriorityCompat(priority: NotificationPriority): Int {
        return when (priority) {
            NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
            NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationPriority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        }
    }
}