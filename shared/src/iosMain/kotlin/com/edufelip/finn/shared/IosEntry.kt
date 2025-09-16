package com.edufelip.finn.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.edufelip.finn.shared.data.fake.PostRepositoryFake
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.navigation.SimpleRouter
import kotlinx.coroutines.flow.flowOf

fun MainViewController() = ComposeUIViewController {
    val router = SimpleRouter()
    val getFeed = GetFeedUseCase(PostRepositoryFake())
    val searchRepo = object : CommunityRepository {
        override fun search(query: String) = flowOf(
            listOf(
                Community(1, "Kotlin", "Kotlin discussions", null, 12345),
                Community(2, "Android", "Android dev", null, 9876),
            ),
        )
        override fun getById(id: Int) = flowOf(
            Community(id, if (id == 1) "Kotlin" else "Android", "Details for $id", null, 1000 + id),
        )
    }
    val searchCommunities = SearchCommunitiesUseCase(searchRepo)
    val getCommunityDetails = GetCommunityDetailsUseCase(searchRepo)
    val getCommunityPosts = GetCommunityPostsUseCase(PostRepositoryFake())
    val observeNotifications: com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase? = null
    val createCommunity = CreateCommunityUseCase(searchRepo)
    val userIdFlow = flowOf("me")
    val getUser = com.edufelip.finn.shared.domain.usecase.GetUserUseCase(object : com.edufelip.finn.shared.domain.repository.UserRepository {
        override fun getUser(id: String) = flowOf(com.edufelip.finn.shared.domain.model.User(id, "iOS User", null, null))
    })
    val postRepo = PostRepositoryFake()
    val commentRepo = object : com.edufelip.finn.shared.domain.repository.CommentRepository {
        override fun list(postId: Int, page: Int) = kotlinx.coroutines.flow.flowOf(emptyList<com.edufelip.finn.shared.domain.model.Comment>())
        override fun add(postId: Int, userId: String, content: String) = kotlinx.coroutines.flow.flowOf(
            com.edufelip.finn.shared.domain.model.Comment(1, postId, userName = "iOS", content = content, dateMillis = kotlin.system.getTimeMillis()),
        )
    }
    val getComments = GetCommentsForPostUseCase(commentRepo)
    val addComment = AddCommentUseCase(commentRepo)
    SharedApp(
        router = router,
        getFeed = getFeed,
        postRepository = postRepo,
        searchCommunities = searchCommunities,
        getCommunityDetails = getCommunityDetails,
        getCommunityPosts = getCommunityPosts,
        observeNotifications = null,
        createCommunity = createCommunity,
        userIdFlow = userIdFlow,
        getUser = getUser,
        getUserPosts = { id, page -> postRepo.feed(id, page) },
        onRequestSignIn = {},
        onRequestSignOut = {},
        createPostRepo = postRepo,
        createPostUserIdProvider = { "me" },
        pickImage = { null },
        getComments = getComments,
        addComment = addComment,
        userIdProvider = { "me" },
        onSharePost = { post ->
            val url = com.edufelip.finn.shared.navigation.DeepLinks.postUrl(post.id)
            presentShareSheet(post.content + "\n" + url)
        },
    )
}
