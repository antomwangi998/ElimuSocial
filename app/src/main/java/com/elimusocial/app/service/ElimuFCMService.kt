package com.elimusocial.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.elimusocial.app.MainActivity
import com.elimusocial.app.R
import com.elimusocial.app.data.repository.FirebaseRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElimuFCMService : FirebaseMessagingService() {

    private val repository = FirebaseRepository()

    companion object {
        // Notification channel IDs
        const val CHANNEL_SOCIAL   = "elimu_social"    // likes, comments, reposts
        const val CHANNEL_MESSAGES = "elimu_messages"  // direct messages
        const val CHANNEL_SYSTEM   = "elimu_system"    // announcements, updates

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(NotificationManager::class.java)

                manager.createNotificationChannel(
                    NotificationChannel(CHANNEL_SOCIAL, "Social", NotificationManager.IMPORTANCE_DEFAULT).apply {
                        description = "Likes, comments, follows and reposts"
                    }
                )
                manager.createNotificationChannel(
                    NotificationChannel(CHANNEL_MESSAGES, "Messages", NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "Direct messages"
                    }
                )
                manager.createNotificationChannel(
                    NotificationChannel(CHANNEL_SYSTEM, "Updates", NotificationManager.IMPORTANCE_LOW).apply {
                        description = "App announcements and updates"
                    }
                )
            }
        }
    }

    // ── Called when FCM token refreshes ───────────────────────────────────
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save new token to Firestore so we can send notifications to this device
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateFcmToken(token)
        }
    }

    // ── Called when a notification arrives ────────────────────────────────
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data["type"] ?: "general"
        val title = message.notification?.title ?: message.data["title"] ?: "Elimu Social"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        val channelId = when (type) {
            "message" -> CHANNEL_MESSAGES
            "like", "comment", "follow", "repost", "mention" -> CHANNEL_SOCIAL
            else -> CHANNEL_SYSTEM
        }

        showNotification(title, body, channelId, type, message.data)
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        type: String,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // Pass data so the app can navigate to the right screen
            putExtra("notification_type", type)
            putExtra("post_id", data["postId"] ?: "")
            putExtra("sender_id", data["senderId"] ?: "")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationIcon = when (type) {
            "like"    -> android.R.drawable.ic_menu_view
            "comment" -> android.R.drawable.ic_menu_edit
            "follow"  -> android.R.drawable.ic_menu_myplaces
            "message" -> android.R.drawable.ic_menu_send
            else      -> android.R.drawable.ic_dialog_info
        }

        val notificationId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(
                if (channelId == CHANNEL_MESSAGES)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}
