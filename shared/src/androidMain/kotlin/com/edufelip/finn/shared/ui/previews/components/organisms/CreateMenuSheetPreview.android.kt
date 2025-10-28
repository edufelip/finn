package com.edufelip.finn.shared.ui.previews.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.organisms.CreateMenuSheet

@Preview(showBackground = true)
@Composable
private fun CreateMenuSheetPreview() {
    ProvideStrings {
        CreateMenuSheet(
            onCreateCommunity = {},
            onCreatePost = {},
        )
    }
}
