package com.edufelip.finn.composeapp.di

import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.LinkActions
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.repository.NotificationsRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.repository.UserRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.DeleteCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunitySubscriptionUseCase
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.domain.usecase.SubscribeToCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.UnsubscribeFromCommunityUseCase
import com.edufelip.finn.shared.presentation.vm.AuthVM
import com.edufelip.finn.shared.presentation.vm.CommentsVM
import com.edufelip.finn.shared.presentation.vm.CommunityDetailsVM
import com.edufelip.finn.shared.presentation.vm.CreateCommunityVM
import com.edufelip.finn.shared.presentation.vm.CreatePostVM
import com.edufelip.finn.shared.presentation.vm.HomeVM
import com.edufelip.finn.shared.presentation.vm.NotificationsVM
import com.edufelip.finn.shared.presentation.vm.ProfileVM
import com.edufelip.finn.shared.presentation.vm.SavedVM
import com.edufelip.finn.shared.presentation.vm.SearchVM
import com.edufelip.finn.shared.notifications.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Provides lightweight in-memory bindings so Desktop/Web hosts can render the Compose UI
 * without relying on backend services. The data is static and intentionally small.
 */
fun stubPlatformBindings(): PlatformBindings {
    val sampleUserId = "guest"
    val sampleCommunity = Community(
        id = 1,
        title = "Compose Multiplatform",
        description = "Discuss the Finn sample app",
        image = null,
        subscribersCount = 4200,
    )
    val samplePosts = listOf(
        Post(
            id = 1,
            content = "Welcome to the Finn sample timeline!",
            communityId = sampleCommunity.id,
            communityTitle = sampleCommunity.title,
            communityImage = null,
            userId = sampleUserId,
            userName = "Compose Bot",
            image = null,
            likesCount = 27,
            commentsCount = 3,
            isLiked = false,
        ),
        Post(
            id = 2,
            content = "This stub feed is powered by ComposeApp",
            communityId = sampleCommunity.id,
            communityTitle = sampleCommunity.title,
            communityImage = null,
            userId = sampleUserId,
            userName = "Compose Bot",
            image = null,
            likesCount = 12,
            commentsCount = 1,
            isLiked = true,
        ),
    )
    val sampleComments = listOf(
        Comment(
            id = 1,
            postId = 1,
            userId = sampleUserId,
            userImage = null,
            userName = "Compose Bot",
            content = "Looks great!",
            dateMillis = null,
        ),
    )
    val sampleUser = User(
        id = sampleUserId,
        name = "Compose User",
        photoUrl = null,
        joinedAtMillis = null,
    )

    val postRepo = object : PostRepository {
        private val postsFlow = MutableStateFlow(samplePosts)
        override fun feed(userId: String, page: Int): Flow<List<Post>> = postsFlow
        override fun postsByCommunity(communityId: Int, page: Int): Flow<List<Post>> = postsFlow
        override fun like(postId: Int, userId: String): Flow<Unit> = flowOf(Unit)
        override fun dislike(postId: Int, userId: String): Flow<Unit> = flowOf(Unit)
        override fun postsByUser(userId: String, page: Int): Flow<List<Post>> = postsFlow
        override fun createPost(content: String, userId: String, image: ByteArray?, communityId: Int?): Flow<Post> =
            flowOf(samplePosts.first().copy(id = 99, content = content, userId = userId))
        override fun delete(postId: Int, userId: String, communityId: Int?): Flow<Unit> = flowOf(Unit)
    }

    val communityRepo = object : CommunityRepository {
        override fun search(query: String): Flow<List<Community>> = flowOf(listOf(sampleCommunity))
        override fun getById(id: Int): Flow<Community> = flowOf(sampleCommunity)
        override fun create(title: String, description: String?, image: ByteArray?): Flow<Community> =
            flowOf(sampleCommunity.copy(id = 2, title = title, description = description))
        override fun subscribe(userId: String, communityId: Int): Flow<Subscription> =
            flowOf(Subscription(id = 1, userId = userId, communityId = communityId))
        override fun unsubscribe(userId: String, communityId: Int): Flow<Unit> = flowOf(Unit)
        override fun getSubscription(userId: String, communityId: Int): Flow<Subscription?> =
            flowOf(Subscription(id = 1, userId = userId, communityId = communityId))
        override fun delete(id: Int): Flow<Unit> = flowOf(Unit)
    }

    val commentRepo = object : CommentRepository {
        override fun list(postId: Int, page: Int): Flow<List<Comment>> = flowOf(sampleComments)
        override fun add(postId: Int, userId: String, content: String): Flow<Comment> =
            flowOf(sampleComments.first().copy(id = 2, postId = postId, content = content))
    }

    val userRepo = object : UserRepository {
        override fun getUser(id: String): Flow<User> = flowOf(sampleUser)
    }

    val notificationsRepo = object : NotificationsRepository {
        override fun observe(): Flow<NotificationItem> = flowOf(NotificationItem(1, "Welcome", "You're viewing stub data."))
    }

    val getFeed = GetFeedUseCase(postRepo)
    val searchCommunities = SearchCommunitiesUseCase(communityRepo)
    val getCommunityDetails = GetCommunityDetailsUseCase(communityRepo)
    val getCommunityPosts = GetCommunityPostsUseCase(postRepo)
    val subscribe = SubscribeToCommunityUseCase(communityRepo)
    val unsubscribe = UnsubscribeFromCommunityUseCase(communityRepo)
    val getSubscription = GetCommunitySubscriptionUseCase(communityRepo)
    val deleteCommunity = DeleteCommunityUseCase(communityRepo)
    val observeNotifications = ObserveNotificationsUseCase(notificationsRepo)
    val createCommunity = CreateCommunityUseCase(communityRepo)
    val getUser = GetUserUseCase(userRepo)
    val addComment = AddCommentUseCase(commentRepo)
    val getComments = GetCommentsForPostUseCase(commentRepo)

    val userIdFlow = MutableStateFlow<String?>(sampleUserId)

    val commentsFactory = object : CommentsVMFactory {
        override fun create(postId: Int): CommentsVM =
            object : CommentsVM {
                override val getComments = getComments
                override val addComment = addComment
                override val userIdProvider: () -> String = { sampleUserId }
            }
    }

    val authActions = object : AuthActions {
        override fun requestSignIn() {}
        override fun requestSignOut() {}
        override fun emailPasswordLogin(email: String, password: String) {}
        override fun createAccount(email: String, password: String) {}
    }
    val shareActions = object : ShareActions {
        override fun share(post: Post) {}
    }
    val linkActions = object : LinkActions {
        override fun openUrl(url: String) {}
    }

    return PlatformBindings(
        homeVm = object : HomeVM {
            override val getFeed = getFeed
            override val postRepository = postRepo
            override val userIdProvider: () -> String = { sampleUserId }
        },
        searchVm = object : SearchVM {
            override val searchCommunities = searchCommunities
        },
        communityVm = object : CommunityDetailsVM {
            override val getCommunityDetails = getCommunityDetails
            override val getCommunityPosts = getCommunityPosts
            override val subscribe = subscribe
            override val unsubscribe = unsubscribe
            override val getSubscription = getSubscription
            override val deleteCommunity = deleteCommunity
        },
        notificationsVm = object : NotificationsVM {
            override val observeNotifications: ObserveNotificationsUseCase = observeNotifications
        },
        createCommunityVm = object : CreateCommunityVM {
            override val createCommunity = createCommunity
        },
        profileVm = object : ProfileVM {
            override val userIdFlow: Flow<String?> = userIdFlow
            override val getUser = getUser
            override val getUserPosts: (String, Int) -> Flow<List<Post>> = { _, _ -> flowOf(samplePosts) }
        },
        savedVm = object : SavedVM {
            override val userIdFlow: Flow<String?> = userIdFlow
            override val repo = postRepo
        },
        authVm = object : AuthVM {
            override val userIdFlow: Flow<String?> = userIdFlow
        },
        createPostVm = object : CreatePostVM {
            override val repo = postRepo
            override val userIdProvider: () -> String = { sampleUserId }
            override val pickImage: suspend () -> ByteArray? = { null }
        },
        commentsFactory = commentsFactory,
        authActions = authActions,
        shareActions = shareActions,
        linkActions = linkActions,
    )
}
