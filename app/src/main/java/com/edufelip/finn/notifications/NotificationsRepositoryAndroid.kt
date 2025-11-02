package com.edufelip.finn.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.edufelip.finn.shared.domain.repository.NotificationsRepository
import com.edufelip.finn.shared.notifications.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationsRepositoryAndroid(private val context: Context) : NotificationsRepository {
    override fun observe(): Flow<NotificationItem> = callbackFlow {
        val filter = IntentFilter(ACTION)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == ACTION) {
                    val id = intent.getIntExtra(EXTRA_ID, 0)
                    val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
                    val body = intent.getStringExtra(EXTRA_BODY) ?: ""
                    trySend(NotificationItem(id, title, body))
                }
            }
        }
        context.registerReceiver(receiver, filter)
        awaitClose { context.unregisterReceiver(receiver) }
    }

    companion object {
        const val ACTION = "com.edufelip.finn.NOTIFICATION"
        const val EXTRA_ID = "id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_BODY = "body"

        fun broadcast(context: Context, item: NotificationItem) {
            val intent = Intent(ACTION).apply {
                putExtra(EXTRA_ID, item.id)
                putExtra(EXTRA_TITLE, item.title)
                putExtra(EXTRA_BODY, item.body)
            }
            context.sendBroadcast(intent)
        }
    }
}
