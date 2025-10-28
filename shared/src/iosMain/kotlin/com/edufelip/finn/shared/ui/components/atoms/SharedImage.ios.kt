package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import org.jetbrains.skia.Image as SkiaImage

@Composable
actual fun SharedImage(url: String, contentDescription: String?, modifier: Modifier) {
    val imageState = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        if (url.isBlank()) {
            imageState.value = null
            return@LaunchedEffect
        }
        imageState.value = loadRemoteImage(url)
    }

    val image = imageState.value
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        PlaceholderBox(modifier = modifier)
    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun loadRemoteImage(url: String): ImageBitmap? = withContext(Dispatchers.Default) {
    val nsUrl = NSURL.URLWithString(url) ?: return@withContext null
    val data: NSData = NSData.dataWithContentsOfURL(nsUrl) ?: return@withContext null
    val bytes = data.toByteArray()
    if (bytes.isEmpty()) return@withContext null
    runCatching { SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = length.toInt()
    if (length == 0) return ByteArray(0)
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        getBytes(pinned.addressOf(0), length.toULong())
    }
    return bytes
}

@Composable
private fun PlaceholderBox(modifier: Modifier) {
    Box(modifier = modifier)
}
