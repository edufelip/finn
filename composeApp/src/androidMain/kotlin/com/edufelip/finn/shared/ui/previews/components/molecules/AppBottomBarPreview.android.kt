package com.edufelip.finn.shared.ui.previews.components.molecules

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.ui.components.molecules.AppBottomBar

@Preview(showBackground = true)
@Composable
private fun AppBottomBarPreview() {
    ProvideStrings {
        AppBottomBar(
            currentRoute = Route.Home,
            onNavigate = {},
            onCreateClick = {},
        )
    }
}
