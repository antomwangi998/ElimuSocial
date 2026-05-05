package com.elimusocial.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.elimusocial.app.data.repository.FirebaseRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElimuFCMService : FirebaseMessagingService() {

    private val repository = FirebaseRepository()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try { repository.updateFcmToken(token) } catch (_: Exception) {}
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    companion object {
        const val CHANNEL_GENERAL  = "elimu_general"
        const val CHANNEL_MESSAGES = "elimu_messages"
        const val CHANNEL_MENTIONS = "elimu_mentions"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                listOf(
                    NotificationChannel(CHANNEL_GENERAL,  "General",  NotificationManager.IMPORTANCE_DEFAULT).apply { description = "General notifications" },
                    NotificationChannel(CHANNEL_MESSAGES, "Messages", NotificationManager.IMPORTANCE_HIGH).apply   { description = "New message alerts" },
                    NotificationChannel(CHANNEL_MENTIONS, "Mentions", NotificationManager.IMPORTANCE_HIGH).apply   { description = "Mentions and replies" },
                ).forEach { manager.createNotificationChannel(it) }
            }
        }
    }
}
