package com.projects.finn.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.projects.finn.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkUserLogged()
    }

    private fun checkUserLogged() {
        if (auth.currentUser != null) {
            openHomePage()
            return
        }
        openAuthActivity()
    }

    private fun openHomePage() {
        startActivity(Intent(this, HomePageActivity::class.java))
        finish()
    }

    private fun openAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}