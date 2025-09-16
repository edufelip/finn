package com.edufelip.finn.shared.notifications

import com.edufelip.finn.shared.presentation.notifications.NotificationItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIApplication
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

object IosPush {
    fun requestAuthorization() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val opts = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options = opts) { granted, error ->
            if (granted) {
                UIApplication.sharedApplication.registerForRemoteNotifications()
            }
        }
    }

    fun handleRemoteNotification(title: String?, body: String?) {
        MainScope().launch {
            NotificationsFacade.emit(NotificationItem(id = (platform.Foundation.NSDate().timeIntervalSince1970 * 1000).toInt(), title = title ?: "", body = body ?: ""))
        }
    }
}
