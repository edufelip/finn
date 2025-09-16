package com.edufelip.finn.shared.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image as SkiaImage

@Composable
actual fun SharedImageBytes(bytes: ByteArray, contentDescription: String?) {
    val skiaImage = SkiaImage.makeFromEncoded(bytes)
    val bitmap = skiaImage.toComposeImageBitmap()
    Image(bitmap = bitmap, contentDescription = contentDescription)
}
