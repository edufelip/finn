package com.projects.finn.utils;

import android.app.Activity;
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
        logoutGoogle(activity);
        //Facebook
        logoutFacebook();
    }

    public static void logoutGoogle(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut();
    }

    public static void logoutFacebook() {
        LoginManager.getInstance().logOut();
    }
}
