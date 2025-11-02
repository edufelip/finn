package com.edufelip.finn.shared.ui.screens.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.DI
import com.edufelip.finn.shared.di.LinkActions
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.i18n.persistLanguageCode
import com.edufelip.finn.shared.i18n.readPersistedLanguageCode
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.navigation.Router
import com.edufelip.finn.shared.presentation.vm.AuthVM
import com.edufelip.finn.shared.presentation.vm.CommunityDetailsVM
import com.edufelip.finn.shared.presentation.vm.CreateCommunityVM
import com.edufelip.finn.shared.presentation.vm.CreatePostVM
import com.edufelip.finn.shared.presentation.vm.HomeVM
import com.edufelip.finn.shared.presentation.vm.NotificationsVM
import com.edufelip.finn.shared.presentation.vm.ProfileVM
import com.edufelip.finn.shared.presentation.vm.SavedVM
import com.edufelip.finn.shared.presentation.vm.SearchVM
import com.edufelip.finn.shared.ui.components.molecules.AppBottomBar
import com.edufelip.finn.shared.ui.components.organisms.AppDrawerContent
import com.edufelip.finn.shared.ui.components.organisms.CreateMenuSheet
import com.edufelip.finn.shared.ui.screens.auth.AuthScreen
import com.edufelip.finn.shared.ui.screens.community.CommunityDetailsScreen
import com.edufelip.finn.shared.ui.screens.community.CreateCommunityScreen
import com.edufelip.finn.shared.ui.screens.home.HomeScreen
import com.edufelip.finn.shared.ui.screens.notifications.NotificationsScreen
import com.edufelip.finn.shared.ui.screens.post.CreatePostScreen
import com.edufelip.finn.shared.ui.screens.post.PostDetailsScreen
import com.edufelip.finn.shared.ui.screens.profile.ProfileScreen
import com.edufelip.finn.shared.ui.screens.saved.SavedScreen
import com.edufelip.finn.shared.ui.screens.search.SearchScreen
import com.edufelip.finn.shared.ui.screens.settings.SettingsScreen
import com.edufelip.finn.shared.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private const val PRIVACY_POLICY_URL = "https://portfolio-edufelip.vercel.app/projects/finn/privacy_policy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedApp(
    router: Router,
) {
    val koin = DI.koin
    val homeVm: HomeVM = koin.get(clazz = HomeVM::class)
    val searchVm: SearchVM = koin.get(clazz = SearchVM::class)
    val communityVm: CommunityDetailsVM = koin.get(clazz = CommunityDetailsVM::class)
    val notificationsVm: NotificationsVM = koin.get(clazz = NotificationsVM::class)
    val createCommunityVm: CreateCommunityVM = koin.get(clazz = CreateCommunityVM::class)
    val profileVm: ProfileVM = koin.get(clazz = ProfileVM::class)
    val savedVm: SavedVM = koin.get(clazz = SavedVM::class)
    val authVm: AuthVM = koin.get(clazz = AuthVM::class)
    val createPostVm: CreatePostVM = koin.get(clazz = CreatePostVM::class)
    val commentsFactory: CommentsVMFactory = koin.get(clazz = CommentsVMFactory::class)
    val authActions: AuthActions = koin.get(clazz = AuthActions::class)
    val shareActions: ShareActions = koin.get(clazz = ShareActions::class)
    val linkActions: LinkActions = koin.get(clazz = LinkActions::class)
    val currentRoute by router.current.collectAsState()

    AppTheme {
        val languageOverrideState = remember { mutableStateOf(readPersistedLanguageCode()) }
        ProvideStrings(localeOverride = languageOverrideState.value) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val showCreateSheet = remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val userInfo = remember { mutableStateOf<User?>(null) }
            val selectedPosts = remember { mutableStateMapOf<Int, Post>() }
            val postUpdates = remember { MutableSharedFlow<Post>(extraBufferCapacity = 16) }
            val postRemovals = remember { MutableSharedFlow<Int>(extraBufferCapacity = 16) }

            val registerPostUpdate: (Post) -> Unit = { post ->
                selectedPosts[post.id] = post
                scope.launch { postUpdates.emit(post) }
            }
            val registerPostRemoval: (Int) -> Unit = { id ->
                selectedPosts.remove(id)
                scope.launch { postRemovals.emit(id) }
            }

            LaunchedEffect(profileVm) {
                profileVm.userIdFlow.filterNotNull().collectLatest { id ->
                    profileVm.getUser(id).collectLatest { result ->
                        when (result) {
                            is Result.Success -> userInfo.value = result.value
                            is Result.Error -> userInfo.value = null
                            Result.Loading -> Unit
                        }
                    }
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawerContent(
                        user = userInfo.value,
                        onNavigate = { route ->
                            scope.launch { drawerState.close() }
                            router.navigate(route)
                        },
                        onLogout = {
                            authActions.requestSignOut()
                            scope.launch { drawerState.close() }
                            router.navigate(Route.Login)
                        },
                        onOpenPrivacy = {
                            linkActions.openUrl(PRIVACY_POLICY_URL)
                            scope.launch { drawerState.close() }
                        },
                    )
                },
            ) {
                if (showCreateSheet.value) {
                    ModalBottomSheet(
                        onDismissRequest = { showCreateSheet.value = false },
                        sheetState = sheetState,
                    ) {
                        CreateMenuSheet(
                            onCreateCommunity = {
                                showCreateSheet.value = false
                                router.navigate(Route.CreateCommunity)
                            },
                            onCreatePost = {
                                showCreateSheet.value = false
                                router.navigate(Route.CreatePost)
                            },
                        )
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (currentRoute != Route.Login) {
                            AppBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { destination -> router.navigate(destination) },
                                onCreateClick = { showCreateSheet.value = true },
                            )
                        }
                    },
                ) { padding ->
                    AppRouteHost(
                        currentRoute = currentRoute,
                        modifier = Modifier.padding(padding),
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigate = { router.navigate(it) },
                        onBack = { router.back() },
                        authVm = authVm,
                        authActions = authActions,
                        homeVm = homeVm,
                        searchVm = searchVm,
                        communityVm = communityVm,
                        notificationsVm = notificationsVm,
                        createCommunityVm = createCommunityVm,
                        profileVm = profileVm,
                        savedVm = savedVm,
                        createPostVm = createPostVm,
                        commentsFactory = commentsFactory,
                        shareActions = shareActions,
                        userInfo = userInfo.value,
                        selectedPosts = selectedPosts,
                        registerPostUpdate = registerPostUpdate,
                        registerPostRemoval = registerPostRemoval,
                        postUpdates = postUpdates,
                        postRemovals = postRemovals,
                        onLanguageApplied = {
                            persistLanguageCode(it)
                            languageOverrideState.value = it
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRouteHost(
    currentRoute: Route,
    modifier: Modifier,
    onOpenDrawer: () -> Unit,
    onNavigate: (Route) -> Unit,
    onBack: () -> Unit,
    authVm: AuthVM,
    authActions: AuthActions,
    homeVm: HomeVM,
    searchVm: SearchVM,
    communityVm: CommunityDetailsVM,
    notificationsVm: NotificationsVM,
    createCommunityVm: CreateCommunityVM,
    profileVm: ProfileVM,
    savedVm: SavedVM,
    createPostVm: CreatePostVM,
    commentsFactory: CommentsVMFactory,
    shareActions: ShareActions,
    userInfo: User?,
    selectedPosts: MutableMap<Int, Post>,
    registerPostUpdate: (Post) -> Unit,
    registerPostRemoval: (Int) -> Unit,
    postUpdates: MutableSharedFlow<Post>,
    postRemovals: MutableSharedFlow<Int>,
    onLanguageApplied: (String?) -> Unit,
) {
    when (val destination = currentRoute) {
        Route.Login -> AuthScreen(
            userIdFlow = authVm.userIdFlow,
            onRequestSignIn = { authActions.requestSignIn() },
            onRequestSignOut = { authActions.requestSignOut() },
            onSignedIn = { onNavigate(Route.Home) },
            onEmailPasswordLogin = { email, password ->
                authActions.emailPasswordLogin(email, password)
            },
            onCreateAccount = { email, password ->
                authActions.createAccount(email, password)
            },
        )

        Route.Home -> HomeScreen(
            getFeed = homeVm.getFeed,
            postRepository = homeVm.postRepository,
            userIdProvider = homeVm.userIdProvider,
            onShare = { post -> shareActions.share(post) },
            modifier = modifier,
            onOpenDrawer = onOpenDrawer,
            onSearchClick = { onNavigate(Route.Search) },
            profileImageUrl = userInfo?.photoUrl,
            onPostClick = { post ->
                selectedPosts[post.id] = post
                onNavigate(Route.PostDetails(post.id))
            },
            onCommunityClick = { communityId -> onNavigate(Route.CommunityDetails(communityId)) },
            onPostStateChanged = { updated -> registerPostUpdate(updated) },
            postUpdates = postUpdates,
            postRemovals = postRemovals,
        )

        Route.Search -> SearchScreen(
            searchCommunities = searchVm.searchCommunities,
            onBack = onBack,
            onCommunityClick = { id -> onNavigate(Route.CommunityDetails(id)) },
            modifier = modifier,
        )

        Route.Notifications -> NotificationsScreen(observe = notificationsVm.observeNotifications)

        Route.Profile -> ProfileScreen(
            userIdFlow = profileVm.userIdFlow,
            getUser = profileVm.getUser,
            getUserPosts = profileVm.getUserPosts,
            goToSaved = { onNavigate(Route.Saved) },
            goToSettings = { onNavigate(Route.Settings) },
        )

        Route.Saved -> SavedScreen(
            userIdFlow = savedVm.userIdFlow,
            repo = savedVm.repo,
            onBack = onBack,
        )

        Route.Settings -> SettingsScreen(
            onApply = { code ->
                onLanguageApplied(code)
                onBack()
            },
        )

        is Route.PostDetails -> {
            val pid = destination.id
            val commentsVmInstance = commentsFactory.create(pid)
            PostDetailsScreen(
                postId = pid,
                post = selectedPosts[pid],
                postRepository = homeVm.postRepository,
                currentUserId = homeVm.userIdProvider(),
                onBack = onBack,
                onShare = { postToShare -> shareActions.share(postToShare) },
                onPostUpdated = { updated -> registerPostUpdate(updated) },
                onPostDeleted = { removedId -> registerPostRemoval(removedId) },
                getComments = commentsVmInstance.getComments,
                addComment = commentsVmInstance.addComment,
                userIdProvider = commentsVmInstance.userIdProvider,
                modifier = modifier,
            )
        }

        is Route.CommunityDetails -> {
            val communityId = destination.id
            CommunityDetailsScreen(
                getCommunityDetails = communityVm.getCommunityDetails,
                getCommunityPosts = communityVm.getCommunityPosts,
                subscribe = communityVm.subscribe,
                unsubscribe = communityVm.unsubscribe,
                getSubscription = communityVm.getSubscription,
                deleteCommunity = communityVm.deleteCommunity,
                postRepository = homeVm.postRepository,
                id = communityId,
                currentUserId = homeVm.userIdProvider(),
                onBack = onBack,
                onPostClick = { post ->
                    selectedPosts[post.id] = post
                    onNavigate(Route.PostDetails(post.id))
                },
                onPostStateChanged = { updated -> registerPostUpdate(updated) },
                onShare = { post -> shareActions.share(post) },
                postUpdates = postUpdates,
                postRemovals = postRemovals,
                modifier = modifier,
            )
        }

        Route.CreateCommunity -> CreateCommunityScreen(
            createCommunity = createCommunityVm.createCommunity,
            onCreated = { id -> onNavigate(Route.CommunityDetails(id)) },
            onCancel = onBack,
            modifier = modifier,
        )

        Route.CreatePost -> CreatePostScreen(
            repo = createPostVm.repo,
            userIdProvider = createPostVm.userIdProvider,
            pickImage = createPostVm.pickImage,
            onCreated = { onNavigate(Route.Home) },
            onCancel = onBack,
            modifier = modifier,
        )
    }
}
