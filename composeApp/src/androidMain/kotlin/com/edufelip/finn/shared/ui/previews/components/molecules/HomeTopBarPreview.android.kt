package com.edufelip.finn.shared.ui.previews.components.molecules

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.components.molecules.HomeTopBar

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun HomeTopBarPreview() {
    ProvideStrings {
        HomeTopBar(
            profileImageUrl = null,
            onMenuClick = {},
            onSearchClick = {},
        )
    }
}
