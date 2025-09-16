package com.edufelip.finn.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.lifecycleScope
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.presentation.vm.HomeVM
import com.edufelip.finn.sharedimpl.CommunityRepositoryAndroid
import com.edufelip.finn.sharedimpl.PostRepositoryAndroid
import com.edufelip.finn.ui.compose.AndroidRouter
import com.edufelip.finn.ui.compose.parseRoute
import com.edufelip.finn.ui.compose.pickImageFlow
import com.edufelip.finn.ui.viewmodels.AuthViewModel
import com.edufelip.finn.ui.viewmodels.CommentsViewModel
import com.edufelip.finn.ui.viewmodels.CommunityDetailsViewModel
import com.edufelip.finn.ui.viewmodels.CreateCommunityViewModel
import com.edufelip.finn.ui.viewmodels.CreatePostViewModel
import com.edufelip.finn.ui.viewmodels.HomeViewModel
import com.edufelip.finn.ui.viewmodels.NotificationsViewModel
import com.edufelip.finn.ui.viewmodels.ProfileViewModel
import com.edufelip.finn.ui.viewmodels.SavedViewModel
import com.edufelip.finn.ui.viewmodels.SearchViewModel
import com.edufelip.finn.utils.GoogleAuthUiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ComposeHomeActivity : ComponentActivity() {

    @Inject lateinit var postRepository: PostRepositoryAndroid

    @Inject lateinit var communityRepository: CommunityRepositoryAndroid

    @Inject lateinit var userRepository: com.edufelip.finn.sharedimpl.UserRepositoryAndroid

    @Inject lateinit var auth: FirebaseAuth

    @Inject lateinit var googleAuthUiClient: GoogleAuthUiClient

    @Inject lateinit var tokenUploader: com.edufelip.finn.notifications.TokenUploaderAndroid

    @Inject lateinit var commentRepository: com.edufelip.finn.sharedimpl.CommentRepositoryAndroid

    private val homeVm: HomeViewModel by viewModels()
    private val searchVm: SearchViewModel by viewModels()
    private val communityVm: CommunityDetailsViewModel by viewModels()
    private val commentsVm: CommentsViewModel by viewModels()
    private val profileVm: ProfileViewModel by viewModels()
    private val savedVm: SavedViewModel by viewModels()
    private val authVm: AuthViewModel by viewModels()
    private val createCommunityVm: CreateCommunityViewModel by viewModels()
    private val createPostVm: CreatePostViewModel by viewModels()
    private val notificationsVm: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restoredPaths: ArrayList<String>? = savedInstanceState?.getStringArrayList("router_paths")
        val deeplinkPath: String? = intent?.getStringExtra("deeplink_path")
        setContent {
            val baseStart = if (auth.currentUser == null) Route.Login else Route.Home
            val start = deeplinkPath?.let { parseRoute(it) } ?: baseStart
            val router = rememberSaveable(
                saver = listSaver(
                    save = { r: AndroidRouter -> r.snapshotPaths() },
                    restore = { paths: List<String> -> AndroidRouter(start).also { it.restorePaths(paths) } },
                ),
            ) { AndroidRouter(start) }
            val getFeed = homeVm.getFeed
            val searchCommunities = searchVm.searchCommunities
            val getCommunityDetails = communityVm.getCommunityDetails
            val getCommunityPosts = communityVm.getCommunityPosts
            val observeNotifications = notificationsVm.observeNotifications
            val createCommunity = createCommunityVm.createCommunity
            val userIdFlow = authVm.userIdFlow
            val getUser = profileVm.getUser
            suspend fun pickImageOnce(): ByteArray? = pickImageFlow().first()
            val pickImage = suspend { pickImageOnce() }
            val getComments = commentsVm.getComments
            val addComment = commentsVm.addComment

            val onRequestSignIn: () -> Unit = {
                this@ComposeHomeActivity.lifecycleScope.launch {
                    googleAuthUiClient.signIn(this@ComposeHomeActivity)
                }
            }
            val onRequestSignOut: () -> Unit = {
                this@ComposeHomeActivity.lifecycleScope.launch { googleAuthUiClient.signOut() }
            }
            // Optional token ping at startup
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tok = task.result
                    this@ComposeHomeActivity.lifecycleScope.launch { tokenUploader.upload(tok) }
                }
            }
            // Restore stack if activity provided an explicit snapshot (e.g., forwarded).
            restoredPaths?.let { router.restorePaths(it) }
            BackHandler(enabled = router.canGoBack) { router.back() }
            val onSharePost: (com.edufelip.finn.shared.domain.model.Post) -> Unit = { post ->
                val url = com.edufelip.finn.shared.navigation.DeepLinks.postUrl(post.id)
                val text = buildString {
                    append(post.content)
                    append('\n')
                    append(url)
                }
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(sendIntent, getString(com.edufelip.finn.R.string.share))
                startActivity(chooser)
            }
            val homeBridge = object : HomeVM {
                override val getFeed = getFeed
                override val postRepository = homeVm.postRepository
                override val userIdProvider = homeVm.userIdProvider
            }
            val searchBridge = object : com.edufelip.finn.shared.presentation.vm.SearchVM { override val searchCommunities = searchCommunities }
            val communityBridge = object : com.edufelip.finn.shared.presentation.vm.CommunityDetailsVM {
                override val getCommunityDetails = getCommunityDetails
                override val getCommunityPosts = getCommunityPosts
            }
            val notificationsBridge = object : com.edufelip.finn.shared.presentation.vm.NotificationsVM { override val observeNotifications = observeNotifications }
            val createCommunityBridge = object : com.edufelip.finn.shared.presentation.vm.CreateCommunityVM { override val createCommunity = createCommunity }
            val profileBridge = object : com.edufelip.finn.shared.presentation.vm.ProfileVM {
                override val userIdFlow = userIdFlow
                override val getUser = getUser
                override val getUserPosts = profileVm.getUserPosts
            }
            val savedBridge = object : com.edufelip.finn.shared.presentation.vm.SavedVM {
                override val userIdFlow = userIdFlow
                override val repo = homeVm.postRepository
            }
            val authBridge = object : com.edufelip.finn.shared.presentation.vm.AuthVM { override val userIdFlow = userIdFlow }
            val createPostBridge = object : com.edufelip.finn.shared.presentation.vm.CreatePostVM {
                override val repo = createPostVm.postRepository
                override val userIdProvider = createPostVm.userIdProvider
                override val pickImage = pickImage
            }
            val commentsFactory: (Int) -> com.edufelip.finn.shared.presentation.vm.CommentsVM = { _ ->
                object : com.edufelip.finn.shared.presentation.vm.CommentsVM {
                    override val getComments = getComments
                    override val addComment = addComment
                    override val userIdProvider = commentsVm.userIdProvider
                }
            }
            com.edufelip.finn.shared.SharedApp(
                router = router,
                homeVm = homeBridge,
                searchVm = searchBridge,
                communityVm = communityBridge,
                notificationsVm = notificationsBridge,
                createCommunityVm = createCommunityBridge,
                profileVm = profileBridge,
                savedVm = savedBridge,
                authVm = authBridge,
                onRequestSignIn = onRequestSignIn,
                onRequestSignOut = onRequestSignOut,
                createPostVm = createPostBridge,
                commentsVmFactory = commentsFactory,
                onSharePost = onSharePost,
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Snapshot of router stack for restoration is handled within setContent scope; no access here.
        // We can no-op here because restoration occurs through intent extras and router's internal state.
    }
}
