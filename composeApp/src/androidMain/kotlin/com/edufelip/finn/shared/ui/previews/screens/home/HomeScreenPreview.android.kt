package com.edufelip.finn.shared.ui.previews.screens.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.home.HomeScreenContent
import com.edufelip.finn.shared.ui.screens.home.HomeUiState

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun HomeScreenPreview() {
    ProvideStrings {
        val samplePost = Post(
            id = 1,
            content = "Compose Multiplatform now powers Finn's home feed!",
            communityId = 12,
            communityTitle = "Compose",
            communityImage = null,
            userId = "user-1",
            userName = "Compose Dev",
            image = null,
            likesCount = 42,
            commentsCount = 5,
            isLiked = true,
            dateMillis = 1_699_756_800_000,
            cachedAtMillis = 1_699_756_800_000,
        )
        val state = remember { HomeUiState(posts = listOf(samplePost)) }
        val refreshState = rememberPullRefreshState(refreshing = false, onRefresh = {})
        HomeScreenContent(
            state = state,
            modifier = Modifier,
            profileImageUrl = null,
            onOpenDrawer = {},
            onSearchClick = {},
            pullRefreshState = refreshState,
            refreshing = false,
            onLoadMore = {},
            onPostClick = {},
            onCommunityClick = {},
            onShare = {},
            onToggleLike = { _, _ -> },
            onHide = {},
            error = null,
        )
    }
}
