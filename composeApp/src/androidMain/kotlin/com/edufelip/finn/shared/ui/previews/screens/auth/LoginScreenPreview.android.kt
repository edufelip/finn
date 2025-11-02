package com.edufelip.finn.shared.ui.previews.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.auth.LoginScreen

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    ProvideStrings {
        LoginScreen(onLogin = {})
    }
}
