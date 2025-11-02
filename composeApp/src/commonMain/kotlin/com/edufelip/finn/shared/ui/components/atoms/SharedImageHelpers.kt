package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

private val sharedImageCache = mutableStateMapOf<String, ImageBitmap?>()

private enum class SharedImagePhase {
    Idle,
    Loading,
    Success,
    Error,
}

@Composable
internal fun SharedImageRemote(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    loader: suspend (String) -> ImageBitmap?,
) {
    val cachedBitmap = sharedImageCache[url]
    val phaseState = remember(url, cachedBitmap) {
        val initialPhase = when {
            url.isBlank() -> SharedImagePhase.Idle
            cachedBitmap != null -> SharedImagePhase.Success
            else -> SharedImagePhase.Loading
        }
        mutableStateOf(initialPhase)
    }
    val bitmapState = remember(url, cachedBitmap) { mutableStateOf(cachedBitmap) }

    LaunchedEffect(url) {
        if (url.isBlank()) {
            phaseState.value = SharedImagePhase.Idle
            bitmapState.value = null
            return@LaunchedEffect
        }
        sharedImageCache[url]?.let { bitmap ->
            bitmapState.value = bitmap
            phaseState.value = SharedImagePhase.Success
            return@LaunchedEffect
        }
        phaseState.value = SharedImagePhase.Loading
        val result = runCatching { loader(url) }
        val loadedBitmap = result.getOrNull()
        if (loadedBitmap != null) {
            sharedImageCache[url] = loadedBitmap
            bitmapState.value = loadedBitmap
            phaseState.value = SharedImagePhase.Success
        } else {
            phaseState.value = SharedImagePhase.Error
        }
    }

    when (phaseState.value) {
        SharedImagePhase.Success -> {
            val bitmap = bitmapState.value
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                )
            } else {
                PlaceholderBox(modifier)
            }
        }

        SharedImagePhase.Loading -> LoadingPlaceholder(modifier)
        SharedImagePhase.Idle, SharedImagePhase.Error -> PlaceholderBox(modifier)
    }
}

@Composable
internal fun SharedImageBytesCommon(
    bytes: ByteArray,
    contentDescription: String?,
    modifier: Modifier,
    decode: (ByteArray) -> ImageBitmap?,
) {
    val bitmap = remember(bytes) { decode(bytes) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        PlaceholderBox(modifier)
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun PlaceholderBox(modifier: Modifier) {
    Box(modifier = modifier)
}
