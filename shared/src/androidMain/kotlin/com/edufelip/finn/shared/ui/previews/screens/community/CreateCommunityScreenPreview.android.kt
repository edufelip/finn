package com.edufelip.finn.shared.ui.previews.screens.community

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.community.CreateCommunityScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private class PreviewCommunityRepository : CommunityRepository {
    override fun search(query: String): Flow<List<Community>> = flowOf(emptyList())

    override fun getById(id: Int): Flow<Community> = flowOf(
        Community(id = id, title = "Compose", description = "Preview", image = null, subscribersCount = 0),
    )

    override fun create(title: String, description: String?, image: ByteArray?) = flowOf(
        Community(
            id = 1,
            title = title,
            description = description,
            image = null,
            subscribersCount = 0,
        ),
    )

    override fun subscribe(userId: String, communityId: Int) = flowOf(
        com.edufelip.finn.shared.domain.model.Subscription(0, userId, communityId),
    )

    override fun unsubscribe(userId: String, communityId: Int): Flow<Unit> = flowOf(Unit)

    override fun getSubscription(userId: String, communityId: Int) = flowOf<com.edufelip.finn.shared.domain.model.Subscription?>(null)

    override fun delete(id: Int): Flow<Unit> = flowOf(Unit)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CreateCommunityScreenPreview() {
    ProvideStrings {
        val repo = remember { PreviewCommunityRepository() }
        CreateCommunityScreen(
            createCommunity = CreateCommunityUseCase(repo),
            onCreated = {},
            onCancel = {},
            modifier = Modifier,
        )
    }
}
