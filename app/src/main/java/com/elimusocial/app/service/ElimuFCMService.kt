package com.elimusocial.app.service

import com.elimusocial.app.data.repository.FirebaseRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElimuFCMService : FirebaseMessagingService() {

    private val repository = FirebaseRepository()

    /**
     * Called when a new FCM token is generated.
     * Saves the token to the user's Firestore profile so the server can send notifications.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.updateFcmToken(token)
            } catch (e: Exception) {
                // Token will be updated next time user logs in
            }
        }
    }

    /**
     * Called when a push notification is received while app is in foreground.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Notification data is available here for custom handling
        // e.g. show in-app notification banner
        val title = message.notification?.title ?: message.data["title"] ?: "Elimu Social"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "general" // like, comment, follow, message
        // TODO: show in-app snackbar or notification
    }
}
