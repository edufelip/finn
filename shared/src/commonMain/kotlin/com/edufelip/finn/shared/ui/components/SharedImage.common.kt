package com.edufelip.finn.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SharedImage(url: String, contentDescription: String?, modifier: Modifier = Modifier)
