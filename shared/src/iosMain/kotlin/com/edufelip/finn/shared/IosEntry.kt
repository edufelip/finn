package com.edufelip.finn.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.edufelip.finn.shared.data.fake.PostRepositoryFake
import com.edufelip.finn.shared.domain.model.Community
cimport com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.repository.UserRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.navigation.DeepLinks
import com.edufelip.finn.shared.navigation.SimpleRouter
import com.edufelip.finn.shared.presentation.vm.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun MainViewController() = ComposeUIViewController {
    val router = SimpleRouter()

    // Fake repositories for iOS demo wiring
    val postRepo: PostRepository = PostRepositoryFake()
    val communityRepo: CommunityRepository = object : CommunityRepository {
        override fun search(query: String) = flowOf(
            listOf(
                Community(1, "Kotlin", "Kotlin discussions", null, 12345),
                Community(2, "Android", "Android dev", null, 9876),
            ),
        )
        override fun getById(id: Int) = flowOf(
            Community(id, if (id == 1) "Kotlin" else "Android", "Details for $id", null, 1000 + id),
        )
        override fun create(title: String, description: String?, image: ByteArray?) = flowOf(
            Community((0..100000).random(), title, description, null, 0),
        )
    }
    val userRepo: UserRepository = object : UserRepository {
        override fun getUser(id: String) = flowOf(com.edufelip.finn.shared.domain.model.User(id, "iOS User", null, null))
    }
    val commentRepo: CommentRepository = object : CommentRepository {
        override fun list(postId: Int, page: Int) = flowOf(emptyList<com.edufelip.finn.shared.domain.model.Comment>())
        override fun add(postId: Int, userId: String, content: String) = flowOf(
            com.edufelip.finn.shared.domain.model.Comment(1, postId, userName = "iOS", content = content, dateMillis = kotlin.system.getTimeMillis()),
        )
    }

    // Use cases
    val getFeed = GetFeedUseCase(postRepo)
    val searchCommunities = SearchCommunitiesUseCase(communityRepo)
    val getCommunityDetails = GetCommunityDetailsUseCase(communityRepo)
    val getCommunityPosts = GetCommunityPostsUseCase(postRepo)
    val createCommunity = CreateCommunityUseCase(communityRepo)
    val getUser = GetUserUseCase(userRepo)
    val getComments = GetCommentsForPostUseCase(commentRepo)
    val addComment = AddCommentUseCase(commentRepo)

    // Bridges (shared VMs)
    val homeBridge = object : HomeVM {
        override val getFeed = getFeed
        override val postRepository = postRepo
        override val userIdProvider: () -> String = { "me" }
    }
    val searchBridge = object : SearchVM { override val searchCommunities = searchCommunities }
    val communityBridge = object : CommunityDetailsVM {
        override val getCommunityDetails = getCommunityDetails
        override val getCommunityPosts = getCommunityPosts
    }
    val notificationsBridge = object : NotificationsVM { override val observeNotifications: com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase? = null }
    val createCommunityBridge = object : CreateCommunityVM { override val createCommunity = createCommunity }
    val profileBridge = object : ProfileVM {
        override val userIdFlow: Flow<String?> = flowOf("me")
        override val getUser = getUser
        override val getUserPosts: (String, Int) -> Flow<List<Post>> = { id, page -> postRepo.feed(id, page) }
    }
    val savedBridge = object : SavedVM {
        override val userIdFlow: Flow<String?> = flowOf("me")
        override val repo = postRepo
    }
    val authBridge = object : AuthVM { override val userIdFlow: Flow<String?> = flowOf("me") }
    val createPostBridge = object : CreatePostVM {
        override val repo = postRepo
        override val userIdProvider: () -> String = { "me" }
        override val pickImage: suspend () -> ByteArray? = { null }
    }
    val commentsFactory: (Int) -> CommentsVM = { _ ->
        object : CommentsVM {
            override val getComments = getComments
            override val addComment = addComment
            override val userIdProvider: () -> String = { "me" }
        }
    }

    SharedApp(
        router = router,
        homeVm = homeBridge,
        searchVm = searchBridge,
        communityVm = communityBridge,
        notificationsVm = notificationsBridge,
        createCommunityVm = createCommunityBridge,
        profileVm = profileBridge,
        savedVm = savedBridge,
        authVm = authBridge,
        onRequestSignIn = {},
        onRequestSignOut = {},
        createPostVm = createPostBridge,
        commentsVmFactory = commentsFactory,
        onSharePost = { post -> presentShareSheet(post.content + "\n" + DeepLinks.postUrl(post.id)) },
    )
}

