package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.projects.finn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.databinding.ActivityForgotPassBinding;


public class ForgotPassActivity extends AppCompatActivity {
    private ActivityForgotPassBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());

        initializeComponents();
        setClickListeners();

        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        auth = FirebaseAuth.getInstance();
    }

    public void setClickListeners() {
        binding.resetButton.setOnClickListener(v -> sendResetEmail());
        binding.fgpassBackButton.setOnClickListener(v -> finish());
    }

    public void sendResetEmail(){
        String email = binding.forgotEmailField.getText().toString().trim();
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
        binding.statusMessage.setText("Se houver um usuário cadastrador você receberá um e-mail para resetar a senha");
        binding.successCheck.startAnimation(fadeIn);
        binding.statusMessage.startAnimation(fadeIn);
        binding.successCheck.setVisibility(View.VISIBLE);
        binding.statusMessage.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::finish, 2000);
    }
}