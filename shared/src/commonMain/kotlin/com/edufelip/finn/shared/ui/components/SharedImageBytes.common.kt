package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SharedImageBytes(bytes: ByteArray, contentDescription: String?, modifier: Modifier = Modifier)
