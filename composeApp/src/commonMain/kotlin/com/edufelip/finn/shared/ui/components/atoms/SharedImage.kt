package com.edufelip.finn.shared.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-platform image loader atom.
 */
@Composable
expect fun SharedImage(url: String, contentDescription: String?, modifier: Modifier = Modifier)
