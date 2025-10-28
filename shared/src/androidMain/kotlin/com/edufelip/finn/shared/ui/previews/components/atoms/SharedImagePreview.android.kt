package com.edufelip.finn.shared.ui.previews.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage

@Preview(showBackground = true)
@Composable
private fun SharedImagePreview() {
    ProvideStrings {
        SharedImage(
            url = "https://picsum.photos/120",
            contentDescription = "Sample",
            modifier = Modifier.size(120.dp),
        )
    }
}
