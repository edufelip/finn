package com.edufelip.finn.shared

import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.data.AppleRemoteConfigCacheTtlProvider
import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.CommentCacheDataSource
import com.edufelip.finn.shared.data.local.CommunityCacheDataSource
import com.edufelip.finn.shared.data.local.PostCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightCommentCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightCommunityCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightPostCacheDataSource
import com.edufelip.finn.shared.data.remote.source.IosCommentRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.IosCommunityRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.IosPostRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.IosUserRemoteDataSource
import com.edufelip.finn.shared.data.repository.DefaultCommentRepository
import com.edufelip.finn.shared.data.repository.DefaultCommunityRepository
import com.edufelip.finn.shared.data.repository.DefaultPostRepository
import com.edufelip.finn.shared.data.repository.DefaultUserRepository
import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.domain.model.Post
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
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.navigation.SimpleRouter
import com.edufelip.finn.shared.presentation.vm.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController {
    val router = SimpleRouter(Route.Login)

    val driver = NativeSqliteDriver(FinnDatabase.Schema, name = "finn.db")
    val database = FinnDatabase(driver)

    val postCache: PostCacheDataSource = SqlDelightPostCacheDataSource(database)
    val communityCache: CommunityCacheDataSource = SqlDelightCommunityCacheDataSource(database)
    val commentCache: CommentCacheDataSource = SqlDelightCommentCacheDataSource(database)
    val cacheTtlProvider: CacheTtlProvider = AppleRemoteConfigCacheTtlProvider()

    val postRepo: PostRepository = DefaultPostRepository(
        remote = IosPostRemoteDataSource(),
        cache = postCache,
        ttlProvider = cacheTtlProvider,
    )

    val communityRepo: CommunityRepository = DefaultCommunityRepository(
        remote = IosCommunityRemoteDataSource(),
        cache = communityCache,
        ttlProvider = cacheTtlProvider,
    )

    val userRepo: UserRepository = DefaultUserRepository(IosUserRemoteDataSource())

    val commentRepo: CommentRepository = DefaultCommentRepository(
        remote = IosCommentRemoteDataSource(),
        cache = commentCache,
        ttlProvider = cacheTtlProvider,
        pageSize = DEFAULT_PAGE_SIZE,
    )


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
    // Start Koin and provide shared UI dependencies
    startKoin {
        modules(
            module {
                single<HomeVM> { homeBridge }
                single<SearchVM> { searchBridge }
                single<CommunityDetailsVM> { communityBridge }
                single<NotificationsVM> { notificationsBridge }
                single<CreateCommunityVM> { createCommunityBridge }
                single<ProfileVM> { profileBridge }
                single<SavedVM> { savedBridge }
                single<AuthVM> { authBridge }
                single<CreatePostVM> { createPostBridge }
                single<CommentsVMFactory> {
                    object : CommentsVMFactory {
                        override fun create(postId: Int): CommentsVM =
                            object : CommentsVM {
                                override val getComments = getComments
                                override val addComment = addComment
                                override val userIdProvider: () -> String = { "me" }
                            }
                    }
                }
                single<AuthActions> {
                    object : AuthActions {
                        override fun requestSignIn() {}
                        override fun requestSignOut() {}
                        override fun emailPasswordLogin(email: String, password: String) {}
                        override fun createAccount(email: String, password: String) {}
                    }
                }
                single<ShareActions> {
                    object : ShareActions {
                        override fun share(post: Post) {
                            presentShareSheet(post.content + "\n" + DeepLinks.postUrl(post.id))
                        }
                    }
                }
                single<com.edufelip.finn.shared.di.LinkActions> {
                    object : com.edufelip.finn.shared.di.LinkActions {
                        override fun openUrl(url: String) {
                            val nsUrl = NSURL.URLWithString(url)
                            if (nsUrl != null) {
                                UIApplication.sharedApplication.openURL(nsUrl)
                            }
                        }
                    }
                }
            },
        )
    }

    SharedApp(router = router)
}

private const val DEFAULT_PAGE_SIZE = 10
