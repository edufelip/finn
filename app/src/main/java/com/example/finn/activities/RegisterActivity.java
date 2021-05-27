package com.example.finn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finn.R;
import com.example.finn.config.FirebaseConfig;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

public class RegisterActivity extends AppCompatActivity {
    private ImageView backButton;
    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerPasswordConfirm;
    private Button registerButton;
    private CheckBox checkBox;
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginButton fbLoginButton;
    private Button fbFakeButton;
    private CallbackManager callbackManager;
    private final static int RC_SIGN_IN = 123;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
        createGoogleRequest();
        createFacebookRequest();
        setClickListeners();
    }

    public void initializeComponents() {
        backButton = findViewById(R.id.backButton);
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerPasswordConfirm = findViewById(R.id.registerPasswordConfirm);
        registerButton = findViewById(R.id.registerButton);
        checkBox = findViewById(R.id.termsCheckBox);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        fbLoginButton = findViewById(R.id.facebook_sign_in_button);
        fbLoginButton.setPermissions("email", "public_profile");
        fbFakeButton = findViewById(R.id.fb_fake_button);
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseConfig.getFirebaseAuth();
    }

    public void setClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        fbFakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginButton.performClick();
            }
        });
    }

    public void registerUser() {
        String name = registerName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();
        String passwordConfirm = registerPasswordConfirm.getText().toString();
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
        if(!checkBox.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    mainPageRedirect();
                } else {
                    String exception = "";
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
            }
        });
    }

    public void createGoogleRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void createFacebookRequest() {
        AppEventsLogger.activateApp(getApplication());
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mainPageRedirect();
                        } else {
                            Log.d("Google Auth", "User couldn't log in");
                        }
                    }
                });
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mainPageRedirect();
                        } else {
                            Log.d("Facebook Auth", "User couldn't log in");
                        }
                    }
                });
    }

    public void mainPageRedirect() {
        startActivity(new Intent(this, MainPageActivity.class));
        finish();
    }
}