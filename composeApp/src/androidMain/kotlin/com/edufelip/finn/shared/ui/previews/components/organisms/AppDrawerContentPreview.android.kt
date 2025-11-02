package com.edufelip.finn.shared.ui.previews.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.organisms.AppDrawerContent

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun AppDrawerContentPreview() {
    ProvideStrings {
        AppDrawerContent(
            user = User(id = "1", name = "Preview User", photoUrl = null, joinedAtMillis = 1_671_609_600_000),
            onNavigate = {},
            onLogout = {},
            onOpenPrivacy = {},
        )
    }
}
