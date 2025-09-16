package com.edufelip.finn.shared

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.edufelip.finn.shared.domain.model.Post
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
) {
    val koin = com.edufelip.finn.shared.di.DI.koin
    val homeVm: HomeVM = koin.get(clazz = HomeVM::class)
    val searchVm: SearchVM = koin.get(clazz = SearchVM::class)
    val communityVm: CommunityDetailsVM = koin.get(clazz = CommunityDetailsVM::class)
    val notificationsVm: NotificationsVM = koin.get(clazz = NotificationsVM::class)
    val createCommunityVm: CreateCommunityVM = koin.get(clazz = CreateCommunityVM::class)
    val profileVm: ProfileVM = koin.get(clazz = ProfileVM::class)
    val savedVm: SavedVM = koin.get(clazz = SavedVM::class)
    val authVm: AuthVM = koin.get(clazz = AuthVM::class)
    val createPostVm: CreatePostVM = koin.get(clazz = CreatePostVM::class)
    val commentsFactory: com.edufelip.finn.shared.di.CommentsVMFactory = koin.get(clazz = com.edufelip.finn.shared.di.CommentsVMFactory::class)
    val authActions: com.edufelip.finn.shared.di.AuthActions = koin.get(clazz = com.edufelip.finn.shared.di.AuthActions::class)
    val shareActions: com.edufelip.finn.shared.di.ShareActions = koin.get(clazz = com.edufelip.finn.shared.di.ShareActions::class)
    val current by router.current.collectAsState()
    AppTheme {
        val langOverrideState = remember { mutableStateOf(readPersistedLanguageCode()) }
        ProvideStrings(localeOverride = langOverrideState.value) {
            Scaffold(
                bottomBar = {
                    if (current != Route.Login) {
                        SharedBottomBar(current = current, onNavigate = { r -> router.navigate(r) })
                    }
                },
                floatingActionButton = {
                    if (current != Route.Login) {
                        FloatingActionButton(onClick = { router.navigate(Route.CreatePost) }) { Text("+") }
                    }
                },
            ) { padding ->
                when (current) {
                    Route.Login -> AuthScreen(
                        userIdFlow = authVm.userIdFlow,
                        onRequestSignIn = { authActions.requestSignIn() },
                        onRequestSignOut = { authActions.requestSignOut() },
                        onSignedIn = { router.navigate(Route.Home) },
                        onEmailPasswordLogin = { e, p -> authActions.emailPasswordLogin(e, p) },
                    )
                    Route.Home -> HomeScreen(
                        getFeed = homeVm.getFeed,
                        postRepository = homeVm.postRepository,
                        userIdProvider = homeVm.userIdProvider,
                        onShare = { post -> shareActions.share(post) },
                    )
                    Route.Search -> SearchScreen(
                        searchCommunities = searchVm.searchCommunities,
                        onBack = { router.back() },
                        onCommunityClick = { id -> router.navigate(Route.CommunityDetails(id)) },
                    )
                    Route.Notifications -> NotificationsScreen(observe = notificationsVm.observeNotifications)
                    Route.Profile -> ProfileScreen(userIdFlow = profileVm.userIdFlow, getUser = profileVm.getUser, getUserPosts = profileVm.getUserPosts, goToSaved = { router.navigate(Route.Saved) }, goToSettings = { router.navigate(Route.Settings) })
                    Route.Saved -> SavedScreen(userIdFlow = savedVm.userIdFlow, repo = savedVm.repo, onBack = { router.back() })
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
                            getComments = commentsFactory.create(pid).getComments,
                            addComment = commentsFactory.create(pid).addComment,
                            userIdProvider = commentsFactory.create(pid).userIdProvider,
                        )
                    }
                    is Route.CommunityDetails -> CommunityDetailsScreen(
                        getCommunityDetails = communityVm.getCommunityDetails,
                        getCommunityPosts = communityVm.getCommunityPosts,
                        id = (current as Route.CommunityDetails).id,
                        onBack = { router.back() },
                        getComments = commentsFactory.create(0).getComments,
                        addComment = commentsFactory.create(0).addComment,
                        userIdProvider = commentsFactory.create(0).userIdProvider,
                    )
                    Route.CreateCommunity -> CreateCommunityScreen(
                        createCommunity = createCommunityVm.createCommunity,
                        onCreated = { id -> router.navigate(Route.CommunityDetails(id)) },
                        onCancel = { router.back() },
                    )
                    Route.CreatePost -> CreatePostScreen(
                        repo = createPostVm.repo,
                        userIdProvider = createPostVm.userIdProvider,
                        pickImage = createPostVm.pickImage,
                        onCreated = { router.navigate(Route.Home) },
                        onCancel = { router.back() },
                    )
                }
            }
        }
    }
}
