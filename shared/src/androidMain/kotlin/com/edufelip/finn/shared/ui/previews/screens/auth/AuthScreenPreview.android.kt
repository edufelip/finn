package com.edufelip.finn.shared.ui.previews.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.auth.AuthScreen
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AuthScreenPreview() {
    ProvideStrings {
        AuthScreen(
            userIdFlow = flowOf(null),
            onRequestSignIn = {},
            onRequestSignOut = {},
            onSignedIn = {},
            onEmailPasswordLogin = { _, _ -> },
            onCreateAccount = { _, _ -> },
        )
    }
}
