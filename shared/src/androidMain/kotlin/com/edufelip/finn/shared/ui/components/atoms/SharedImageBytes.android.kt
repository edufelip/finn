package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
actual fun SharedImageBytes(bytes: ByteArray, contentDescription: String?, modifier: Modifier) {
    AsyncImage(model = bytes, contentDescription = contentDescription, modifier = modifier)
}
