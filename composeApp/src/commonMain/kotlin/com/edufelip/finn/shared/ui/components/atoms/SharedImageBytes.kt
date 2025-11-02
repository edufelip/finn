package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Display an image from raw bytes.
 */
@Composable
expect fun SharedImageBytes(bytes: ByteArray, contentDescription: String?, modifier: Modifier = Modifier)
