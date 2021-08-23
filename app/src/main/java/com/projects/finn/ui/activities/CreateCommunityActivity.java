package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AlphaAnimation;

import com.bumptech.glide.Glide;
import com.projects.finn.databinding.ActivityCreateCommunityBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class CreateCommunityActivity extends AppCompatActivity {
    private ActivityCreateCommunityBinding binding;
    private Boolean isNextAllowed;
    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCommunityBinding.inflate(getLayoutInflater());

        initializeComponents();
        setClickListeners();

        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        fadeOutAnim();
        binding.createCommunityNextButton.setClickable(false);
    }

    public void initializeComponents() {
        isNextAllowed = false;
    }

    public void setClickListeners() {
        binding.createCommunityNameInput.addTextChangedListener(new TextWatcher() {
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

        binding.createCommunityAboutInput.addTextChangedListener(new TextWatcher() {
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

        binding.createCommunityNextButton.setOnClickListener(v -> {
            //send request to check
        });

        binding.createCommunityBackButton.setOnClickListener(v -> finish());

        binding.createCommunityIconSelect.setOnClickListener(v -> pickImageFromGalery());
    }

    public void checkCanGoNext() {
        String name = binding.createCommunityNameInput.getText().toString();
        String about = binding.createCommunityAboutInput.getText().toString();
        Boolean checked = binding.createCommunityCheckbox.isChecked();
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
        binding.createCommunityNextButton.setClickable(isNextAllowed);
    }

    public void fadeInAnim() {
        AlphaAnimation fadeInAnim = new AlphaAnimation(0.5f, 1.0f);
        fadeInAnim.setDuration(600);
        fadeInAnim.setFillAfter(true);
        binding.createCommunityNextButton.startAnimation(fadeInAnim);
    }

    public void fadeOutAnim() {
        AlphaAnimation fadeOutAnim = new AlphaAnimation(1.0f, 0.5f);
        fadeOutAnim.setDuration(600);
        fadeOutAnim.setFillAfter(true);
        binding.createCommunityNextButton.startAnimation(fadeOutAnim);
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
                binding.createCommunityIcon.setImageBitmap(bitmap);

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
                .into(binding.createCommunityIcon);
    }

    private void launchImageCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }
}