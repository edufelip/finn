package com.projects.finn.utils

import android.app.Activity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.projects.finn.BuildConfig

class Authentication {
    companion object {
        fun logout(auth: FirebaseAuth, activity: Activity?) {
            //Firebase
            auth.signOut()
            //Google
            logoutGoogle(activity)
            //Facebook
            logoutFacebook()
        }

        fun logoutGoogle(activity: Activity?) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
            mGoogleSignInClient.signOut()
        }

        fun logoutFacebook() {
            LoginManager.getInstance().logOut()
        }
    }
}