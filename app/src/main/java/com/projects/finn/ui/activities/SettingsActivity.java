package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.R;
import com.projects.finn.databinding.ActivitySettingsBinding;
import com.projects.finn.ui.viewmodels.SettingsViewModel;
import com.projects.finn.utils.Authentication;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;
    private ActivitySettingsBinding binding;
    private SettingsViewModel mSettingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        initializeViewModel();
        setClickListeners();
        setContentView(binding.getRoot());
    }

    public void initializeViewModel() {
        mSettingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        mSettingsViewModel.observeDelete().observe(this, delete -> {
            if(delete.getId().equals("1")) {
                if(auth.getCurrentUser() != null) {
                    auth.getCurrentUser().delete().addOnCompleteListener(task -> {
                        Toast.makeText(this, "Succesfully deleted, we'll miss you!", Toast.LENGTH_SHORT).show();
                        Authentication.logoutGoogle(this);
                        Authentication.logoutFacebook();
                        finish();
                        MainPageActivity.mainPageActivity.finish();
                        startActivity(new Intent(this, AuthActivity.class));
                    });
                }
            } else {
                Toast.makeText(this, "Something wrong happened, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                printNotAvailabe();
            }
        });

        binding.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                printNotAvailabe();
            }
        });

        binding.deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                if(auth.getCurrentUser() != null) {
                    String id = auth.getCurrentUser().getUid();
                    mSettingsViewModel.deleteUser(id);
                }
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {

            });
            builder.setTitle("Delete Account");
            builder.setMessage("Are you sure you want to delete your account?");
            builder.create().show();
        });
    }

    public void printNotAvailabe() {
        Toast.makeText(this, "This option is not available yet", Toast.LENGTH_SHORT).show();
    }
}