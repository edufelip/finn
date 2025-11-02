package com.edufelip.finn.shared.ui.previews.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.search.SearchScreen
import kotlinx.coroutines.flow.flowOf

private class PreviewCommunityRepository : com.edufelip.finn.shared.domain.repository.CommunityRepository {
    private val sampleCommunity = Community(
        id = 1,
        title = "Compose",
        description = "Compose enthusiasts",
        image = null,
        subscribersCount = 1200,
    )

    override fun search(query: String) = flowOf(listOf(sampleCommunity))
    override fun getById(id: Int) = flowOf(sampleCommunity.copy(id = id))
    override fun create(title: String, description: String?, image: ByteArray?) = flowOf(sampleCommunity.copy(title = title, description = description))
    override fun subscribe(userId: String, communityId: Int) = flowOf(com.edufelip.finn.shared.domain.model.Subscription(0, userId, communityId))
    override fun unsubscribe(userId: String, communityId: Int) = flowOf(Unit)
    override fun getSubscription(userId: String, communityId: Int) = flowOf<com.edufelip.finn.shared.domain.model.Subscription?>(null)
    override fun delete(id: Int) = flowOf(Unit)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchScreenPreview() {
    ProvideStrings {
        SearchScreen(
            searchCommunities = SearchCommunitiesUseCase(PreviewCommunityRepository()),
            onBack = {},
            onCommunityClick = {},
            modifier = Modifier,
        )
    }
}
