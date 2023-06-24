package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

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
                        Toast.makeText(this, getResources().getString(R.string.successfully_deleted_miss_you), Toast.LENGTH_SHORT).show();
                        Authentication.Companion.logoutGoogle(this);
                        Authentication.Companion.logoutFacebook();
                        Intent intent = new Intent(this, AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
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
            builder.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {

            });
            builder.setTitle(getResources().getString(R.string.delete_account));
            builder.setMessage(getResources().getString(R.string.sure_delete_account));
            builder.create().show();
        });
    }

    public void printNotAvailabe() {
        Toast.makeText(this, getResources().getString(R.string.option_not_available), Toast.LENGTH_SHORT).show();
    }
}