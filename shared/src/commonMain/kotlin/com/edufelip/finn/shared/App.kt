package com.edufelip.finn.shared

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.i18n.readPersistedLanguageCode
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.navigation.Router
import com.edufelip.finn.shared.presentation.auth.AuthScreen
import com.edufelip.finn.shared.presentation.community.CommunityDetailsScreen
import com.edufelip.finn.shared.presentation.community.CreateCommunityScreen
import com.edufelip.finn.shared.presentation.home.HomeScreen
import com.edufelip.finn.shared.presentation.notifications.NotificationsScreen
import com.edufelip.finn.shared.presentation.post.CreatePostScreen
import com.edufelip.finn.shared.presentation.post.PostDetailsScreen
import com.edufelip.finn.shared.presentation.profile.ProfileScreen
import com.edufelip.finn.shared.presentation.saved.SavedScreen
import com.edufelip.finn.shared.presentation.search.SearchScreen
import com.edufelip.finn.shared.ui.components.SharedBottomBar
import com.edufelip.finn.shared.ui.theme.AppTheme

@Composable
fun SharedApp(
    router: Router,
    getFeed: com.edufelip.finn.shared.domain.usecase.GetFeedUseCase,
    postRepository: com.edufelip.finn.shared.domain.repository.PostRepository,
    searchCommunities: SearchCommunitiesUseCase,
    getCommunityDetails: GetCommunityDetailsUseCase,
    getCommunityPosts: GetCommunityPostsUseCase,
    observeNotifications: ObserveNotificationsUseCase?,
    createCommunity: CreateCommunityUseCase,
    userIdFlow: kotlinx.coroutines.flow.Flow<String?>,
    getUser: GetUserUseCase,
    getUserPosts: (String, Int) -> kotlinx.coroutines.flow.Flow<com.edufelip.finn.shared.domain.model.Post>,
    onRequestSignIn: () -> Unit,
    onRequestSignOut: () -> Unit,
    createPostRepo: com.edufelip.finn.shared.domain.repository.PostRepository,
    createPostUserIdProvider: () -> String,
    pickImage: suspend () -> ByteArray?,
    getComments: GetCommentsForPostUseCase,
    addComment: AddCommentUseCase,
    userIdProvider: () -> String,
    onSharePost: (Post) -> Unit,
) {
    val current by router.current.collectAsState()
    AppTheme {
        val langOverrideState = remember { mutableStateOf(readPersistedLanguageCode()) }
        ProvideStrings(localeOverride = langOverrideState.value) {
            Scaffold(
                bottomBar = { SharedBottomBar(current = current, onNavigate = { r -> router.navigate(r) }) },
                floatingActionButton = {
                    androidx.compose.material3.FloatingActionButton(onClick = { router.navigate(Route.CreatePost) }) { Text("+") }
                },
            ) { padding ->
                when (current) {
                    Route.Login -> AuthScreen(
                        userIdFlow = userIdFlow,
                        onRequestSignIn = onRequestSignIn,
                        onRequestSignOut = onRequestSignOut,
                        onSignedIn = { router.navigate(Route.Home) },
                    )
                    Route.Home -> HomeScreen(
                        getFeed = getFeed,
                        postRepository = postRepository,
                        userIdProvider = userIdProvider,
                        onShare = onSharePost,
                    )
                    Route.Search -> SearchScreen(
                        searchCommunities = searchCommunities,
                        onBack = { router.back() },
                        onCommunityClick = { id -> router.navigate(Route.CommunityDetails(id)) },
                    )
                    Route.Notifications -> NotificationsScreen(observe = observeNotifications)
                    Route.Profile -> ProfileScreen(userIdFlow = userIdFlow, getUser = getUser, getUserPosts = getUserPosts, goToSaved = { router.navigate(Route.Saved) }, goToSettings = { router.navigate(Route.Settings) })
                    Route.Saved -> SavedScreen(userIdFlow = userIdFlow, repo = postRepository, onBack = { router.back() })
                    Route.Settings -> com.edufelip.finn.shared.presentation.settings.SettingsScreen(onApply = { code ->
                        com.edufelip.finn.shared.i18n.persistLanguageCode(code)
                        langOverrideState.value = code
                        router.back()
                    })
                    is Route.PostDetails -> {
                        val pid = (current as Route.PostDetails).id
                        PostDetailsScreen(
                            postId = pid,
                            onBack = { router.back() },
                            getComments = getComments,
                            addComment = addComment,
                            userIdProvider = userIdProvider,
                        )
                    }
                    is Route.CommunityDetails -> CommunityDetailsScreen(
                        getCommunityDetails = getCommunityDetails,
                        getCommunityPosts = getCommunityPosts,
                        id = (current as Route.CommunityDetails).id,
                        onBack = { router.back() },
                        getComments = getComments,
                        addComment = addComment,
                        userIdProvider = userIdProvider,
                    )
                    Route.CreateCommunity -> CreateCommunityScreen(
                        createCommunity = createCommunity,
                        onCreated = { id -> router.navigate(Route.CommunityDetails(id)) },
                        onCancel = { router.back() },
                    )
                    Route.CreatePost -> CreatePostScreen(
                        repo = createPostRepo,
                        userIdProvider = createPostUserIdProvider,
                        pickImage = pickImage,
                        onCreated = { router.navigate(Route.Home) },
                        onCancel = { router.back() },
                    )
                }
            }
        }
    }
}
