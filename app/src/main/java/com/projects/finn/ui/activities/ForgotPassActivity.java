package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.finn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgotPassActivity extends AppCompatActivity {
    private MaterialButton resetButton;
    private EditText emailField;
    private FirebaseAuth auth;
    private TextView statusMessage;
    private ImageView successCheck;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        initializeComponents();
        setClickListeners();
    }

    public void initializeComponents() {
        resetButton = findViewById(R.id.resetButton);
        emailField = findViewById(R.id.forgot_email_field);
        statusMessage = findViewById(R.id.status_message);
        successCheck = findViewById(R.id.success_check);
        backButton = findViewById(R.id.fgpass_back_button);
        auth = FirebaseAuth.getInstance();
    }

    public void setClickListeners() {
        resetButton.setOnClickListener(v -> sendResetEmail());
        backButton.setOnClickListener(v -> finish());
    }

    public void sendResetEmail(){
        String email = emailField.getText().toString().trim();
        if(email.isEmpty()) return;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            sendSuccessMessage();
        });
    }

    public void sendSuccessMessage() {
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        statusMessage.setText("Se houver um usuário cadastrador você receberá um e-mail para resetar a senha");
        successCheck.startAnimation(fadeIn);
        statusMessage.startAnimation(fadeIn);
        successCheck.setVisibility(View.VISIBLE);
        statusMessage.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::finish, 2000);
    }
}