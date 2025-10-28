package com.edufelip.finn.shared.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

        LanguageOption(label = strings.system_default, selected = choice == null) { choice = null }
        LanguageOption(label = strings.english, selected = choice == "en") { choice = "en" }
        LanguageOption(label = strings.portuguese, selected = choice == "pt") { choice = "pt" }
        LanguageOption(label = strings.spanish, selected = choice == "es") { choice = "es" }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { onApply(choice) }) { Text(strings.apply) }
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onSelected: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onSelected)
        Text(label)
    }
}
