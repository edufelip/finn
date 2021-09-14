package com.projects.finn.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.databinding.ActivityCreateCommunityBinding;
import com.projects.finn.models.Community;
import com.projects.finn.ui.viewmodels.CreateCommunityViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class CreateCommunityActivity extends AppCompatActivity {
    private ActivityCreateCommunityBinding binding;
    private Boolean isNextAllowed;
    private CreateCommunityViewModel mCreateCommunityViewModel;
    private final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    @Inject
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCommunityBinding.inflate(getLayoutInflater());

        initializeViewModel();
        initializeComponents();
        setTextChangeListeners();
        setClickListeners();
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isNextAllowed) {
            fadeOutAnim();
        }
        checkCanGoNext();
        binding.createCommunityNextButton.setClickable(isNextAllowed);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void initializeComponents() {
        isNextAllowed = false;
    }

    public void initializeViewModel() {
        mCreateCommunityViewModel = new ViewModelProvider(this).get(CreateCommunityViewModel.class);
        mCreateCommunityViewModel.observeCommunity().observe(this, community -> {
                if(community.getId() == (-1)) {
                    switch (community.getTitle()) {
                        case "Conflict":
                            Toast.makeText(this, "This name is unavailable", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(this, "Something wrong happened, try again later", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                    return;
                }
                redirectToNewCommunity(community);
            }
        );
    }

    public void setClickListeners() {
        binding.createCommunityNextButton.setOnClickListener(v -> {
            Community createComm = new Community();
            createComm.setTitle(binding.createCommunityNameInput.getText().toString());
            createComm.setDescription(binding.createCommunityAboutInput.getText().toString());
            createComm.setUser_id(auth.getCurrentUser().getUid());
            Bitmap bitmap = ((BitmapDrawable)binding.createCommunityIcon.getDrawable()).getBitmap();
            MultipartBody.Part commImage = buildImageBodyPart("community", bitmap);
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, createComm.toJson());
            mCreateCommunityViewModel.createCommunity(requestBody, commImage);
        });

        binding.createCommunityCheckbox.setOnClickListener(v -> {
            checkCanGoNext();
        });

        binding.createCommunityBackButton.setOnClickListener(v -> finish());

        binding.createCommunityIconSelect.setOnClickListener(v -> pickImageFromGalery());
    }

    public void setTextChangeListeners() {
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
    }

    public void redirectToNewCommunity(Community community) {
        Intent intent = new Intent(this, CommunityActivity.class);
        intent.putExtra("community", community);
        startActivity(intent);
        finish();
    }

    public void checkCanGoNext() {
        String name = binding.createCommunityNameInput.getText().toString();
        String about = binding.createCommunityAboutInput.getText().toString();
        Boolean checked = binding.createCommunityCheckbox.isChecked();
        if(name.isEmpty() || about.isEmpty() || !checked) {
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
        pickGalleryResultLauncher.launch(gallery);
    }

    ActivityResultLauncher<Intent> pickGalleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    imageUri = result.getData().getData();
                    try {
                        launchImageCrop(imageUri);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        binding.createCommunityIcon.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(CreateCommunityActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


    private MultipartBody.Part buildImageBodyPart(String fileName, Bitmap bitmap) {
        File leftImageFile = convertBitmapToFile(fileName, bitmap);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), leftImageFile);
        return MultipartBody.Part.createFormData(fileName, leftImageFile.getName(), reqFile);
    }

    private File convertBitmapToFile(String fileName, Bitmap bitmap) {
        File file = new File(getApplicationContext().getFilesDir(), fileName + ".png");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitMapData = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fos.write(bitMapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}