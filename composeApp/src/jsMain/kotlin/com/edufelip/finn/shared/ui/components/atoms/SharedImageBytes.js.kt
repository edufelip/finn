package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image as SkiaImage

@Composable
actual fun SharedImageBytes(bytes: ByteArray, contentDescription: String?, modifier: Modifier) {
    SharedImageBytesCommon(
        bytes = bytes,
        contentDescription = contentDescription,
        modifier = modifier,
        decode = ::decodeImageBytes,
    )
}

private fun decodeImageBytes(bytes: ByteArray): ImageBitmap? =
    runCatching { SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
