package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.jetbrains.skia.Image as SkiaImage

@Composable
actual fun SharedImage(url: String, contentDescription: String?, modifier: Modifier) {
    SharedImageRemote(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        loader = ::loadImageBitmapFromUrl,
    )
}

private suspend fun loadImageBitmapFromUrl(url: String): ImageBitmap? =
    runCatching { fetchBytes(url) }
        .mapCatching { decodeImageBytes(it) }
        .getOrNull()

private suspend fun fetchBytes(url: String): ByteArray {
    val response = window.fetch(url).await()
    val buffer: ArrayBuffer = response.arrayBuffer().await()
    return buffer.toByteArray()
}

private fun decodeImageBytes(bytes: ByteArray): ImageBitmap? =
    runCatching { SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

private fun ArrayBuffer.toByteArray(): ByteArray {
    val int8 = Int8Array(this)
    return ByteArray(int8.length) { index -> int8[index] }
}

private operator fun Int8Array.get(index: Int): Byte =
    (asDynamic()[index] as Int).toByte()
