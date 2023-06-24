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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.R;
import com.projects.finn.databinding.ActivityCreatePostBinding;
import com.projects.finn.domain.models.Community;
import com.projects.finn.domain.models.Post;
import com.projects.finn.ui.viewmodels.CreatePostViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class CreatePostActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;
    @Inject
    RequestManager glide;
    private ActivityCreatePostBinding binding;
    private CreatePostViewModel mCreatePostViewModel;
    private String[] communityNames;
    private List<Community> communities = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());

        initializeViewModel();
        setClickListeners();
        setContentView(binding.getRoot());
    }

    public void initializeViewModel() {
        String id = auth.getCurrentUser().getUid();

        mCreatePostViewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        mCreatePostViewModel.observeUserCommunities().observe(this, communities -> {
            this.communities = communities;
            communityNames = new String[communities.size()];
            communities.forEach(community -> {
                communityNames[communities.indexOf(community)] = community.getTitle();
            });
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_item, communityNames);
            binding.communityField.setAdapter(adapter);
        });

        mCreatePostViewModel.observePost().observe(this, post -> {
            if(post.getId() == -1) {
                Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.post_successfully_created), Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        mCreatePostViewModel.getCommunitiesFromUser(id);
    }

    public void setClickListeners() {
        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.fabGallery.setOnClickListener(view -> {
            pickImageFromGallery();
        });

        binding.createButton.setOnClickListener(view -> {
            if(checkInputs()) {
                createPost();
            }
        });
    }

    public void pickImageFromGallery() {
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
                    Uri imageUri = result.getData().getData();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        result.getData();
                        try {
                            setImage(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CreatePostActivity.this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    public void setImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(binding.postImage);
    }

    public boolean checkInputs() {
        boolean isPickCommunityEmpty = binding.communityField.getText().toString().equals("Pick a community");
        boolean isPostContentEmpty = binding.postTextarea.getText().toString().isEmpty();
        if(isPickCommunityEmpty || isPostContentEmpty) {
            Toast.makeText(this, getResources().getString(R.string.please_fill_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void createPost() {
        String communityTitle = binding.communityField.getText().toString();
        int commId = this.communities.stream()
                .filter(community -> community.getTitle().equals(communityTitle))
                .collect(Collectors.toList())
                .get(0)
                .getId();
        String userId = auth.getCurrentUser().getUid();

        Post post = new Post();
        post.setContent(binding.postTextarea.getText().toString());
        post.setCommunityId(commId);
        post.setUserId(userId);


        Bitmap bitmap = binding.postImage.getDrawable() != null ? ((BitmapDrawable)binding.postImage.getDrawable()).getBitmap() : null;
        MultipartBody.Part postImage = buildImageBodyPart("post", bitmap);
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, post.toJson());
        mCreatePostViewModel.createPost(requestBody, postImage);
    }

    private MultipartBody.Part buildImageBodyPart(String fileName, Bitmap bitmap) {
        if(bitmap == null) return MultipartBody.Part.createFormData(fileName, "", RequestBody.create(MediaType.parse("image/*"), ""));
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