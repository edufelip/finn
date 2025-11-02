package com.edufelip.finn.shared.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

object IosPush {
    @OptIn(ExperimentalForeignApi::class)
    fun requestAuthorization() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val opts = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options = opts) { granted, error ->
            if (granted) {
                UIApplication.sharedApplication().registerForRemoteNotifications()
            }
        }
    }

    fun handleRemoteNotification(title: String?, body: String?) {
        MainScope().launch {
            val timestamp = (NSDate().timeIntervalSince1970 * 1000).toInt()
            NotificationsFacade.emit(NotificationItem(id = timestamp, title = title.orEmpty(), body = body.orEmpty()))
        }
    }
}
