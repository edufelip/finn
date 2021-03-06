package com.projects.finn.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;
import com.projects.finn.databinding.ActivityAuthBinding;
import com.projects.finn.data.repositories.UserRepository;
import com.projects.finn.utils.Verify;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;
    @Inject
    UserRepository userRepository;
    public static Activity authActivity;
    private ActivityAuthBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        authActivity = this;

        initializeComponents();
        createGoogleRequest();
        createFacebookRequest();
        setClickListeners();

        TextView tv = (TextView) binding.googleSignInButton.getChildAt(0);
        tv.setText(getString(R.string.signgoogle));

        String error = getIntent().getStringExtra("Error");
        if(error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }

        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        binding.googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        binding.facebookSignInButton.setPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
    }

    public void setClickListeners() {
        binding.fbFakeButton.setOnClickListener(v -> binding.facebookSignInButton.performClick());
        binding.googleSignInButton.setOnClickListener(v -> resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent())));
        binding.loginButton.setOnClickListener(v -> authenticateUser());
        binding.redirectRegister.setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, RegisterActivity.class)));
        binding.forgotPassButton.setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, ForgotPassActivity.class)));
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleAccessToken(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });

    public void authenticateUser() {
        String email = binding.loginEmail.getText().toString();
        String password = binding.loginPassword.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Verify.isEmailValid(email)) {
            Toast.makeText(this, getResources().getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    mainPageRedirect();
                } else {
                    String exception;
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = getResources().getString(R.string.user_not_registered);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = getResources().getString(R.string.password_incorrect);
                    } catch (Exception e) {
                        exception = "Error " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(AuthActivity.this, getResources().getString(R.string.error) + ": " + exception, Toast.LENGTH_SHORT).show();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                    Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void mainPageRedirect() {
        startActivity(new Intent(this, MainPageActivity.class));
        finish();
    }
}