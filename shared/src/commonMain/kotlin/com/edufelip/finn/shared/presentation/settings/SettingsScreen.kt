package com.edufelip.finn.shared.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.LocalStrings

@Composable
fun SettingsScreen(onApply: (String?) -> Unit) {
    val strings = LocalStrings.current
    var choice by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.settings)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(selected = choice == null, onClick = { choice = null })
            Text(strings.system_default)
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(selected = choice == "en", onClick = { choice = "en" })
            Text(strings.english)
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(selected = choice == "pt", onClick = { choice = "pt" })
            Text(strings.portuguese)
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(selected = choice == "es", onClick = { choice = "es" })
            Text(strings.spanish)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onApply(choice) }) { Text(strings.apply) }
    }
}
