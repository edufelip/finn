package com.edufelip.finn.shared.ui.previews.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.settings.SettingsScreen

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    ProvideStrings {
        SettingsScreen(onApply = {})
    }
}
