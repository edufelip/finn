package com.edufelip.finn.shared.ui.previews.screens.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.notifications.NotificationItem
import com.edufelip.finn.shared.ui.screens.notifications.NotificationsScreen
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NotificationsScreenPreview() {
    ProvideStrings {
        NotificationsScreen(
            observe = null,
            eventsFlow = flowOf(NotificationItem(1, "Welcome", "Compose previews are live")),
        )
    }
}
