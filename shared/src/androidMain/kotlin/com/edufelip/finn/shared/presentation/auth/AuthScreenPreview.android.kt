package com.edufelip.finn.shared.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AuthScreen_Preview() {
    ProvideStrings(localeOverride = null) {
        AuthScreen(
            userIdFlow = flowOf(null),
            onRequestSignIn = {},
            onRequestSignOut = {},
            onSignedIn = {},
            onEmailPasswordLogin = { _, _ -> },
        )
    }
}

