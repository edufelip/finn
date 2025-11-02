package com.edufelip.finn.shared.ui.previews.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImageBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Preview(showBackground = true)
@Composable
private fun SharedImageBytesPreview() {
    val sampleBytes = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=")
    ProvideStrings {
        SharedImageBytes(
            bytes = sampleBytes,
            contentDescription = "Sample Bytes",
            modifier = Modifier.size(120.dp),
        )
    }
}
