package com.projects.finn.utils;

import android.app.Activity;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.BuildConfig;

public class Authentication {
    public static void logout(FirebaseAuth auth, Activity activity) {
        //Firebase
        auth.signOut();
        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut();
        //Facebook
        LoginManager.getInstance().logOut();
    }
}
