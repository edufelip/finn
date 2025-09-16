package com.edufelip.finn.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openHomePage()
    }

    private fun openHomePage() {
        val deeplinkPath = intent?.data?.let { uri -> mapDeepLink(uri) }
        val i = Intent(this, ComposeHomeActivity::class.java)
        if (deeplinkPath != null) i.putExtra("deeplink_path", deeplinkPath)
        startActivity(i)
        finish()
    }

    private fun openAuthActivity() {
        openHomePage()
    }

    private fun mapDeepLink(uri: Uri): String? {
        val path = (uri.path ?: "").trim('/', ' ')
        val host = uri.host ?: ""
        // Accept both custom scheme (finn://community/123) and generic https links.
        return when {
            path.startsWith("community/") -> path
            path.startsWith("post/") -> path
            path == "home" || path == "login" || path == "search" || path == "notifications" || path == "profile" || path == "createCommunity" || path == "saved" || path == "createPost" || path == "settings" -> path
            // If host is like app domain and path empty, go home
            host.contains("finn", ignoreCase = true) && path.isEmpty() -> "home"
            else -> null
        }
    }
}
