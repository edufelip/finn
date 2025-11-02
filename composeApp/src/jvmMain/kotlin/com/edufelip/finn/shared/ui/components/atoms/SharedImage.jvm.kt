package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import java.net.URL

@Composable
actual fun SharedImage(url: String, contentDescription: String?, modifier: Modifier) {
    SharedImageRemote(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        loader = ::loadImageBitmapFromUrl,
    )
}

private suspend fun loadImageBitmapFromUrl(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        URL(url).openStream().use { input -> input.readBytes() }
    }.mapCatching { bytes ->
        SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
    }.getOrNull()
}
