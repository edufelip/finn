package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.finn.BuildConfig;
import com.projects.finn.R;
import com.projects.finn.config.FirebaseConfig;
import com.projects.finn.databinding.ActivityAuthBinding;
import com.projects.finn.utils.Checkers;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());

        initializeComponents();
        createGoogleRequest();
        createFacebookRequest();
        setClickListeners();

        TextView tv = (TextView) binding.googleSignInButton.getChildAt(0);
        tv.setText(getString(R.string.signgoogle));

        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        binding.googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        binding.facebookSignInButton.setPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseConfig.getFirebaseAuth();
    }

    public void setClickListeners() {
        binding.loginButton.setOnClickListener(v -> authenticateUser());

        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        binding.fbFakeButton.setOnClickListener(v -> binding.facebookSignInButton.performClick());

        binding.redirectRegister.setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, RegisterActivity.class)));

        binding.forgotPassButton.setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, ForgotPassActivity.class)));
    }

    public void authenticateUser() {
        String email = binding.loginEmail.getText().toString();
        String password = binding.loginPassword.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(this, "Please enter your e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Checkers.isEmailValid(email)) {
            Toast.makeText(this, "The given email is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    mainPageRedirect();
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = "This user is not registered";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "The password is incorrect";
                    } catch (Exception e) {
                        exception = "Error " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(AuthActivity.this, "Error" + exception, Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void createGoogleRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void createFacebookRequest() {
        AppEventsLogger.activateApp(getApplication());
        binding.facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleAccessToken(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleAccessToken(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    mainPageRedirect();
                } else {
                    Log.d("Google Auth", "User couldn't log in");
                }
            });
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        mainPageRedirect();
                    } else {
                        Log.d("Facebook Auth", "User couldn't log in");
                    }
                });
    }

    public void mainPageRedirect() {
        startActivity(new Intent(this, MainPageActivity.class));
        finish();
    }
}