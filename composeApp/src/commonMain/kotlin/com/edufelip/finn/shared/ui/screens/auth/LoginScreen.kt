package com.edufelip.finn.shared.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.edufelip.finn.shared.i18n.LocalStrings

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(strings.login_placeholder)
        Button(onClick = onLogin) { Text(strings.continue_label) }
    }
}
