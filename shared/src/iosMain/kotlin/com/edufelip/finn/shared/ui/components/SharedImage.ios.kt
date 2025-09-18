package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun SharedImage(url: String, contentDescription: String?, modifier: Modifier) {
    KamelImage(resource = asyncPainterResource(url), contentDescription = contentDescription, modifier = modifier)
}
