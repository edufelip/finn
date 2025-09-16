package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun SharedImage(url: String, contentDescription: String?) {
    KamelImage(resource = asyncPainterResource(url), contentDescription = contentDescription)
}
