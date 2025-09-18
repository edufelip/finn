package com.edufelip.finn.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.DI
import com.edufelip.finn.shared.di.LinkActions
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.i18n.ProvideStrings
import com.edufelip.finn.shared.i18n.persistLanguageCode
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
import com.edufelip.finn.shared.presentation.settings.SettingsScreen
import com.edufelip.finn.shared.presentation.vm.AuthVM
import com.edufelip.finn.shared.presentation.vm.CommunityDetailsVM
import com.edufelip.finn.shared.presentation.vm.CreateCommunityVM
import com.edufelip.finn.shared.presentation.vm.CreatePostVM
import com.edufelip.finn.shared.presentation.vm.HomeVM
import com.edufelip.finn.shared.presentation.vm.NotificationsVM
import com.edufelip.finn.shared.presentation.vm.ProfileVM
import com.edufelip.finn.shared.presentation.vm.SavedVM
import com.edufelip.finn.shared.presentation.vm.SearchVM
import com.edufelip.finn.shared.ui.components.SharedBottomBar
import com.edufelip.finn.shared.ui.components.SharedImage
import com.edufelip.finn.shared.ui.theme.AppTheme
import com.edufelip.finn.shared.util.format.formatJoined
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
    val current by router.current.collectAsState()
    AppTheme {
        val langOverrideState = remember { mutableStateOf(readPersistedLanguageCode()) }
        ProvideStrings(localeOverride = langOverrideState.value) {
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
                    profileVm.getUser(id).collectLatest { user -> userInfo.value = user }
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
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
                        SheetContent(
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
                        if (current != Route.Login) {
                            SharedBottomBar(
                                current = current,
                                onNavigate = { r -> router.navigate(r) },
                                onCreateClick = {
                                    showCreateSheet.value = true
                                },
                            )
                        }
                    },
                ) { padding ->
                    when (val destination = current) {
                        Route.Login -> AuthScreen(
                            userIdFlow = authVm.userIdFlow,
                            onRequestSignIn = { authActions.requestSignIn() },
                            onRequestSignOut = { authActions.requestSignOut() },
                            onSignedIn = { router.navigate(Route.Home) },
                            onEmailPasswordLogin = { e, p -> authActions.emailPasswordLogin(e, p) },
                            onCreateAccount = { e, p -> authActions.createAccount(e, p) },
                        )

                        Route.Home -> HomeScreen(
                            getFeed = homeVm.getFeed,
                            postRepository = homeVm.postRepository,
                            userIdProvider = homeVm.userIdProvider,
                            onShare = { post -> shareActions.share(post) },
                            modifier = Modifier.padding(padding),
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            },
                            onSearchClick = { router.navigate(Route.Search) },
                            profileImageUrl = userInfo.value?.photoUrl,
                            onPostClick = { post ->
                                selectedPosts[post.id] = post
                                router.navigate(Route.PostDetails(post.id))
                            },
                            onCommunityClick = { communityId -> router.navigate(Route.CommunityDetails(communityId)) },
                            onPostStateChanged = { updated -> registerPostUpdate(updated) },
                            postUpdates = postUpdates,
                            postRemovals = postRemovals,
                        )

                        Route.Search -> SearchScreen(
                            searchCommunities = searchVm.searchCommunities,
                            onBack = { router.back() },
                            onCommunityClick = { id -> router.navigate(Route.CommunityDetails(id)) },
                            modifier = Modifier.padding(padding),
                        )

                        Route.Notifications -> NotificationsScreen(observe = notificationsVm.observeNotifications)

                        Route.Profile -> ProfileScreen(
                            userIdFlow = profileVm.userIdFlow,
                            getUser = profileVm.getUser,
                            getUserPosts = profileVm.getUserPosts,
                            goToSaved = { router.navigate(Route.Saved) },
                            goToSettings = { router.navigate(Route.Settings) },
                        )

                        Route.Saved -> SavedScreen(
                            userIdFlow = savedVm.userIdFlow,
                            repo = savedVm.repo,
                            onBack = { router.back() },
                        )

                        Route.Settings -> SettingsScreen(
                            onApply = { code ->
                                persistLanguageCode(code)
                                langOverrideState.value = code
                                router.back()
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
                                onBack = { router.back() },
                                onShare = { postToShare -> shareActions.share(postToShare) },
                                onPostUpdated = { updated -> registerPostUpdate(updated) },
                                onPostDeleted = { removedId -> registerPostRemoval(removedId) },
                                getComments = commentsVmInstance.getComments,
                                addComment = commentsVmInstance.addComment,
                                userIdProvider = commentsVmInstance.userIdProvider,
                                modifier = Modifier.padding(padding),
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
                                onBack = { router.back() },
                                onPostClick = { post ->
                                    selectedPosts[post.id] = post
                                    router.navigate(Route.PostDetails(post.id))
                                },
                                onPostStateChanged = { updated -> registerPostUpdate(updated) },
                                onShare = { post -> shareActions.share(post) },
                                postUpdates = postUpdates,
                                postRemovals = postRemovals,
                                modifier = Modifier.padding(padding),
                            )
                        }

                        Route.CreateCommunity -> CreateCommunityScreen(
                            createCommunity = createCommunityVm.createCommunity,
                            onCreated = { id -> router.navigate(Route.CommunityDetails(id)) },
                            onCancel = { router.back() },
                            modifier = Modifier.padding(padding),
                        )

                        Route.CreatePost -> CreatePostScreen(
                            repo = createPostVm.repo,
                            userIdProvider = createPostVm.userIdProvider,
                            pickImage = createPostVm.pickImage,
                            onCreated = { router.navigate(Route.Home) },
                            onCancel = { router.back() },
                            modifier = Modifier.padding(padding),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetContent(
    onCreateCommunity: () -> Unit,
    onCreatePost: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = strings.create, style = MaterialTheme.typography.titleMedium)
        ListItem(
            headlineContent = { Text(strings.create_community) },
            supportingContent = { Text(strings.create_community_description) },
            leadingContent = { Icon(Icons.Filled.People, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().clickable { onCreateCommunity() },
        )
        ListItem(
            headlineContent = { Text(strings.create_post) },
            supportingContent = { Text(strings.create_post_description) },
            leadingContent = { Icon(Icons.Filled.Edit, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().clickable { onCreatePost() },
        )
    }
}

@Composable
private fun DrawerContent(
    user: User?,
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit,
    onOpenPrivacy: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (user != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                user.photoUrl?.takeIf { it.isNotBlank() }?.let { url ->
                    SharedImage(
                        url = url,
                        contentDescription = strings.profile,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } ?: Icon(Icons.Filled.Person, contentDescription = strings.profile)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(user.name ?: strings.user, style = MaterialTheme.typography.titleMedium)
                    user.joinedAtMillis?.let { Text(text = formatJoined(it), style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        NavigationDrawerItem(
            label = { Text(strings.home) },
            selected = false,
            onClick = { onNavigate(Route.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.profile) },
            selected = false,
            onClick = { onNavigate(Route.Profile) },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.saved) },
            selected = false,
            onClick = { onNavigate(Route.Saved) },
            icon = { Icon(Icons.Filled.Bookmark, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.settings) },
            selected = false,
            onClick = { onNavigate(Route.Settings) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.privacy_policy) },
            selected = false,
            onClick = onOpenPrivacy,
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
        )
        Spacer(Modifier.height(24.dp))
        NavigationDrawerItem(
            label = { Text(strings.sign_out) },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
        )
    }
}
