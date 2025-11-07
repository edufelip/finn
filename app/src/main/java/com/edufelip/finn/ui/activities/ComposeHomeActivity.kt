package com.edufelip.finn.ui.activities

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.lifecycleScope
import com.edufelip.finn.R
import com.edufelip.finn.composeapp.FinnApp
import com.edufelip.finn.composeapp.di.PlatformBindings
import com.edufelip.finn.composeapp.di.composePlatformModule
import com.edufelip.finn.notifications.TokenUploaderAndroid
import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.DI
import com.edufelip.finn.shared.di.LinkActions
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.navigation.DeepLinks
import com.edufelip.finn.shared.navigation.Route
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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import javax.inject.Inject

@AndroidEntryPoint
class ComposeHomeActivity : ComponentActivity() {

    @Inject lateinit var auth: FirebaseAuth

    @Inject lateinit var googleAuthUiClient: GoogleAuthUiClient

    @Inject lateinit var tokenUploader: TokenUploaderAndroid

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
            val baseStart = Route.Login
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
            val onSharePost: (Post) -> Unit = { post ->
                val url = DeepLinks.postUrl(post.id)
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
                val chooser = Intent.createChooser(sendIntent, getString(R.string.share))
                startActivity(chooser)
            }
            val homeBridge = object : HomeVM {
                override val getFeed = getFeed
                override val postRepository = homeVm.postRepository
                override val userIdProvider = homeVm.userIdProvider
            }
            val searchBridge = object : SearchVM {
                override val searchCommunities = searchCommunities
            }
            val communityBridge = object : CommunityDetailsVM {
                override val getCommunityDetails = getCommunityDetails
                override val getCommunityPosts = getCommunityPosts
                override val subscribe = communityVm.subscribe
                override val unsubscribe = communityVm.unsubscribe
                override val getSubscription = communityVm.getSubscription
                override val deleteCommunity = communityVm.deleteCommunity
            }
            val notificationsBridge = object : NotificationsVM {
                override val observeNotifications = observeNotifications
            }
            val createCommunityBridge = object : CreateCommunityVM {
                override val createCommunity = createCommunity
            }
            val profileBridge = object : ProfileVM {
                override val userIdFlow = userIdFlow
                override val getUser = getUser
                override val getUserPosts = profileVm.getUserPosts
            }
            val savedBridge = object : SavedVM {
                override val userIdFlow = userIdFlow
                override val repo = homeVm.postRepository
            }
            val authBridge = object : AuthVM {
                override val userIdFlow = userIdFlow
            }
            val createPostBridge = object : CreatePostVM {
                override val repo = createPostVm.postRepository
                override val userIdProvider = createPostVm.userIdProvider
                override val pickImage = pickImage
            }
            val commentsFactory = object : CommentsVMFactory {
                override fun create(postId: Int): CommentsVM =
                    object : CommentsVM {
                        override val getComments = getComments
                        override val addComment = addComment
                        override val userIdProvider = commentsVm.userIdProvider
                    }
            }
            val authActions = object : AuthActions {
                override fun requestSignIn() = onRequestSignIn()
                override fun requestSignOut() = onRequestSignOut()
                override fun emailPasswordLogin(email: String, password: String) {
                    when {
                        email.isBlank() -> Toast.makeText(this@ComposeHomeActivity, "Please enter email", Toast.LENGTH_SHORT).show()
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(this@ComposeHomeActivity, "Email is invalid", Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(this@ComposeHomeActivity, "Please enter password", Toast.LENGTH_SHORT).show()
                        !isOnline() -> Toast.makeText(this@ComposeHomeActivity, "No internet connection", Toast.LENGTH_SHORT).show()
                        else -> auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    val msg = when (val ex = task.exception) {
                                        is FirebaseAuthInvalidUserException -> "User not registered"
                                        is FirebaseAuthInvalidCredentialsException -> "Password incorrect"
                                        is FirebaseNetworkException -> "Network error. Check your connection and try again"
                                        else -> ex?.message ?: "Unknown error"
                                    }
                                    Toast.makeText(this@ComposeHomeActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                override fun createAccount(email: String, password: String) {
                    when {
                        email.isBlank() -> Toast.makeText(this@ComposeHomeActivity, "Please enter email", Toast.LENGTH_SHORT).show()
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(this@ComposeHomeActivity, "Email is invalid", Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(this@ComposeHomeActivity, "Please enter password", Toast.LENGTH_SHORT).show()
                        !isOnline() -> Toast.makeText(this@ComposeHomeActivity, "No internet connection", Toast.LENGTH_SHORT).show()
                        else -> auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    val msg = when (val ex = task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                                        is FirebaseNetworkException -> "Network error. Check your connection and try again"
                                        else -> ex?.message ?: "Unknown error"
                                    }
                                    Toast.makeText(this@ComposeHomeActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@ComposeHomeActivity, "Account created. You are now signed in.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
            val shareActions = object : ShareActions {
                override fun share(post: Post) = onSharePost(post)
            }
            val linkActions = object : LinkActions {
                override fun openUrl(url: String) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    this@ComposeHomeActivity.startActivity(intent)
                }
            }
            val bindings = PlatformBindings(
                homeVm = homeBridge,
                searchVm = searchBridge,
                communityVm = communityBridge,
                notificationsVm = notificationsBridge,
                createCommunityVm = createCommunityBridge,
                profileVm = profileBridge,
                savedVm = savedBridge,
                authVm = authBridge,
                createPostVm = createPostBridge,
                commentsFactory = commentsFactory,
                authActions = authActions,
                shareActions = shareActions,
                linkActions = linkActions,
            )
            // Load Koin bindings for shared UI (bridges + platform actions)
            loadKoinModules(composePlatformModule(bindings))

            DI.configure { GlobalContext.get() }
            FinnApp(router = router)
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Snapshot of router stack for restoration is handled within setContent scope; no access here.
        // We can no-op here because restoration occurs through intent extras and router's internal state.
    }
}
