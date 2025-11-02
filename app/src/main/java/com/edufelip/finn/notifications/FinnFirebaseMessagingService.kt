package com.edufelip.finn.notifications

import com.edufelip.finn.shared.notifications.NotificationItem
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FinnFirebaseMessagingService : FirebaseMessagingService() {
    @Inject lateinit var tokenUploader: TokenUploaderAndroid
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: ""
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val id = System.currentTimeMillis().toInt()
        NotificationsRepositoryAndroid.broadcast(applicationContext, NotificationItem(id, title, body))
        NotificationUtils.notify(applicationContext, id, title, body)
    }

    override fun onNewToken(token: String) {
        scope.launch { tokenUploader.upload(token) }
    }
}
