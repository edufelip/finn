package com.example.finn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CreateCommunityActivity extends AppCompatActivity {
    private TextInputEditText communityName;
    private TextInputEditText communityAbout;
    private MaterialButton nextButton;
    private ImageButton backButton;
    Boolean isNextAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);
        initializeComponents();
        fadeOutAnim();
        setClickListeners();
        nextButton.setClickable(false);
    }

    public void initializeComponents() {
        communityName = findViewById(R.id.create_community_name_input);
        communityAbout = findViewById(R.id.create_community_about_input);
        nextButton = findViewById(R.id.create_community_next_button);
        backButton = findViewById(R.id.create_community_back_button);
        isNextAllowed = false;
    }

    public void setClickListeners() {
        communityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanGoNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        communityAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanGoNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request to check
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void checkCanGoNext() {
        String name = communityName.getText().toString();
        String about = communityAbout.getText().toString();
        if(name.isEmpty() || about.isEmpty()) {
            if(isNextAllowed) {
                switchNextAllowed();
                fadeOutAnim();
            }
        } else {
            if(!isNextAllowed) {
                switchNextAllowed();
                fadeInAnim();
            }
        }
    }

    public void switchNextAllowed() {
        isNextAllowed = !isNextAllowed;
        nextButton.setClickable(isNextAllowed);
    }

    public void fadeInAnim() {
        AlphaAnimation fadeInAnim = new AlphaAnimation(0.5f, 1.0f);
        fadeInAnim.setDuration(600);
        fadeInAnim.setFillAfter(true);
        nextButton.startAnimation(fadeInAnim);
    }

    public void fadeOutAnim() {
        AlphaAnimation fadeOutAnim = new AlphaAnimation(1.0f, 0.5f);
        fadeOutAnim.setDuration(600);
        fadeOutAnim.setFillAfter(true);
        nextButton.startAnimation(fadeOutAnim);

    }
}