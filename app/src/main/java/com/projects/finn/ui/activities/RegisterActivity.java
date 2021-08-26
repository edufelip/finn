package com.projects.finn.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.projects.finn.BuildConfig;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.projects.finn.databinding.ActivityRegisterBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;
    private ActivityRegisterBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        initializeComponents();
        createGoogleRequest();
        createFacebookRequest();
        setClickListeners();

        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        binding.googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        binding.facebookSignInButton.setPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
    }

    public void setClickListeners() {
        binding.fbFakeButton.setOnClickListener(v -> binding.facebookSignInButton.performClick());
        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.googleSignInButton.setOnClickListener(v -> resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent())));
        binding.registerBackButton.setOnClickListener(v -> finish());
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleAccessToken(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });

    public void registerUser() {
        String name = binding.registerName.getText().toString();
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String passwordConfirm = binding.registerPasswordConfirm.getText().toString();
        if(name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordConfirm.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter your password again", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "The passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!binding.termsCheckBox.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                mainPageRedirect();
            } else {
                String exception;
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    exception = "Digite uma senha mais forte!";
                } catch(FirebaseAuthInvalidCredentialsException e) {
                    exception = "Digite um e-mail válido";
                } catch(FirebaseAuthUserCollisionException e) {
                    exception = "Esse e-mail já está cadastrado";
                } catch(Exception e) {
                    exception = "Erro ao cadastrar o usuário" + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
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
                        Log.d("Facebook Auth", "User couldn't log in");
                    }
                });
    }

    public void mainPageRedirect() {
        startActivity(new Intent(this, MainPageActivity.class));
        finish();
    }
}