package com.example.finn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;

public class CreateCommunityActivity extends AppCompatActivity {
    private TextInputEditText communityName;
    private TextInputEditText communityAbout;
    private MaterialCheckBox checkBox;
    private MaterialButton nextButton;
    private ImageButton backButton;
    private ConstraintLayout iconPick;
    private ImageView icon;
    private Boolean isNextAllowed;
    private int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);
        initializeComponents();
        setClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fadeOutAnim();
        nextButton.setClickable(false);
    }

    public void initializeComponents() {
        communityName = findViewById(R.id.create_community_name_input);
        communityAbout = findViewById(R.id.create_community_about_input);
        checkBox = findViewById(R.id.create_community_checkbox);
        nextButton = findViewById(R.id.create_community_next_button);
        backButton = findViewById(R.id.create_community_back_button);
        iconPick = findViewById(R.id.create_community_icon_select);
        icon = findViewById(R.id.create_community_icon);
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
        
        iconPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGalery();
            }
        });
    }

    public void checkCanGoNext() {
        String name = communityName.getText().toString();
        String about = communityAbout.getText().toString();
        Boolean checked = checkBox.isChecked();
        if(name.isEmpty() || about.isEmpty() || checked) {
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
    
    public void pickImageFromGalery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        ArrayList<String> mimeTypes = new ArrayList<String>();
        mimeTypes.add("image/jpeg");
        mimeTypes.add("image/png");
        mimeTypes.add("image/jpg");
        gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        gallery.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    Uri imageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                launchImageCrop(imageUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                icon.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == Activity.RESULT_OK) {
                if(result.getUri() != null) setIcon(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }

    private void setIcon(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(icon);
    }

    private void launchImageCrop(Uri imageUri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this);
    }
}