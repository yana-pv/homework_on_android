package com.example.homework3.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import androidx.core.app.RemoteInput
import com.example.homework3.model.UserMessage
import com.example.homework3.service.MessageRepository
import com.example.homework3.utils.Keys
import com.example.homework3.R

class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInput?.getCharSequence(Keys.Actions.REPLY_ACTION_KEY)?.toString()

        if (!replyText.isNullOrEmpty()) {
            val message = UserMessage(
                text = context.getString(R.string.reply_prefix, replyText),
                isFromReply = true
            )
            MessageRepository.addMessage(message)

            val notificationId = intent.getIntExtra(Keys.Intent.NOTIFICATION_ID, Keys.Notification.DEFAULT_NOTIFICATION_ID)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }
}