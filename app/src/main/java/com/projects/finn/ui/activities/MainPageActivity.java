package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;
import com.projects.finn.databinding.ActivityMainPageBinding;
import com.projects.finn.databinding.NavHeaderBinding;
import com.projects.finn.ui.activities.homeFragments.ChatFragment;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.ui.activities.homeFragments.HomeFragment;
import com.projects.finn.ui.activities.homeFragments.NotificationsFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainPageActivity extends AppCompatActivity implements HandleClick {
    @Inject
    FirebaseAuth auth;
    @Inject
    RequestManager glide;
    private ActivityMainPageBinding binding;
    private ActionBarDrawerToggle toggle;
    private HomeFragment homeFragment;
    private ChatFragment chatFragment;
    private NotificationsFragment notificationsFragment;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());

        initializeComponents();
        setupBottomNavigationView(savedInstanceState);
        setupNavigationDrawer();
        updateNavUserInfo();
        setupClickListeners();
        setContentView(binding.getRoot());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("opened_fragment", binding.bottomNavigationView.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    public void initializeComponents() {
        homeFragment = new HomeFragment();
        homeFragment.setInterface(MainPageActivity.this);
        chatFragment = new ChatFragment();
        notificationsFragment = new NotificationsFragment();
    }

    public void updateNavUserInfo() {
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0));
        headerBinding.displayName.setText(auth.getCurrentUser().getDisplayName());
        headerBinding.displayEmail.setText(auth.getCurrentUser().getEmail());
        glide.load(auth.getCurrentUser().getPhotoUrl().toString()).into(headerBinding.profilePictureIv);
    }

    public void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.drawer_profile):
                case (R.id.drawer_posts):
                    startActivity(new Intent(MainPageActivity.this, ProfileActivity.class));
                    break;
                case (R.id.drawer_saved):
                    startActivity(new Intent(MainPageActivity.this, SavedActivity.class));
                    break;
                case (R.id.drawer_settings):
                    startActivity(new Intent(MainPageActivity.this, SettingsActivity.class));
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    public void setupBottomNavigationView(Bundle savedInstanceState) {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.iconHome):
                    setCurrentFragment(homeFragment);
                    break;
                case (R.id.iconAdd):
                    bottomSheetDialog = new BottomSheetDialog(MainPageActivity.this);
                    View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                            findViewById(R.id.bottom_sheet));
                    bottomSheetDialog.setContentView(sheetView);
                    setDialogClickListeners();
                    bottomSheetDialog.show();
                    break;
                case (R.id.iconChat):
                    setCurrentFragment(chatFragment);
                    break;
                case (R.id.iconNotification):
                    setCurrentFragment(notificationsFragment);
                    break;
                default:
                    return false;
            }
            return true;
        });
        BadgeDrawable badge = binding.bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
        badge.setNumber(1);
        badge.setVisible(true);

        if(savedInstanceState==null) setCurrentFragment(homeFragment);
    }

    public void setupClickListeners() {
        binding.logoutButton.setOnClickListener(v -> {
            //Firebase
            auth.signOut();
            //Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut();
            //Facebook
            LoginManager.getInstance().logOut();

            startActivity(new Intent(MainPageActivity.this, AuthActivity.class));
            finish();
        });
    }

    public void setDialogClickListeners() {
        bottomSheetDialog.findViewById(R.id.create_bottom_dialog_community_button).setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, CreateCommunityActivity.class));
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.create_bottom_dialog_post_button).setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, CreatePostActivity.class));
            bottomSheetDialog.dismiss();
        });
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.flFragment, fragment)
            .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void buttonClicked(View v) {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }
}