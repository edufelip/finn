package com.edufelip.finn.shared.ui.previews.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.repository.UserRepository
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.flow.flowOf

private val previewUserRepository = object : UserRepository {
    override fun getUser(id: String) = flowOf(
        User(id = id, name = "Preview User", photoUrl = null, joinedAtMillis = 1_699_756_800_000),
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ProfileScreenPreview() {
    ProvideStrings {
        ProfileScreen(
            userIdFlow = flowOf("preview"),
            getUser = GetUserUseCase(previewUserRepository),
            getUserPosts = { _, _ -> flowOf(listOf(samplePost())) },
            goToSaved = {},
            goToSettings = {},
            modifier = Modifier,
        )
    }
}

private fun samplePost() = Post(
    id = 1,
    content = "Compose Multiplatform profile preview",
    communityId = null,
    communityTitle = null,
    communityImage = null,
    userId = "preview",
    userName = "Preview",
    image = null,
    likesCount = 3,
    commentsCount = 1,
    isLiked = false,
    dateMillis = 1_699_756_800_000,
)
