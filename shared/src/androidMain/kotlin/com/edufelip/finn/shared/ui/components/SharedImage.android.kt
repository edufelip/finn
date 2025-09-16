package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@Composable
actual fun SharedImage(url: String, contentDescription: String?) {
    AsyncImage(model = url, contentDescription = contentDescription)
}
