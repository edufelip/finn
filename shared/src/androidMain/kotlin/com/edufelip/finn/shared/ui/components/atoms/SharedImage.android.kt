package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
actual fun SharedImage(url: String, contentDescription: String?, modifier: Modifier) {
    AsyncImage(model = url, contentDescription = contentDescription, modifier = modifier)
}
