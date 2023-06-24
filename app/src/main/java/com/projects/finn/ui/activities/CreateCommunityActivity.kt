package com.projects.finn.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.Guidelines
import com.google.firebase.auth.FirebaseAuth
import com.projects.finn.R
import com.projects.finn.databinding.ActivityCreateCommunityBinding
import com.projects.finn.domain.models.Community
import com.projects.finn.ui.viewmodels.CreateCommunityViewModel
import com.projects.finn.utils.extensions.shortToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class CreateCommunityActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth
    private var _binding: ActivityCreateCommunityBinding? = null
    private val binding get() = _binding!!
    private var isNextAllowed: Boolean? = null
    private val mCreateCommunityViewModel by viewModels<CreateCommunityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateCommunityBinding.inflate(layoutInflater)
        initializeViewModel()
        initializeComponents()
        setTextChangeListeners()
        setClickListeners()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (!isNextAllowed!!)
            fadeOutAnim()
        checkCanGoNext()
        binding.createCommunityNextButton.isClickable = isNextAllowed!!
    }

    private fun initializeComponents() {
        isNextAllowed = false
    }

    private fun initializeViewModel() {
        mCreateCommunityViewModel.observeCommunity().observe(
            this
        ) { community: Community ->
            if (community.id == -1) {
                when (community.title) {
                    "Conflict" -> Toast.makeText(
                        this,
                        resources.getString(R.string.name_unavailable),
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.error_try_again_later),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
                return@observe
            }
            redirectToNewCommunity(community)
        }
    }

    fun setClickListeners() {
        binding.createCommunityNextButton.setOnClickListener {
            val createComm = Community().apply {
                title = binding.createCommunityNameInput.text.toString()
                description = binding.createCommunityAboutInput.text.toString()
                userId = auth.currentUser!!.uid
            }
            val bitmap =
                (binding.createCommunityIcon.drawable as BitmapDrawable).bitmap
            val commImage = buildImageBodyPart("community", bitmap)
            val requestBody =
                RequestBody.create(MultipartBody.FORM, createComm.toJson())
            mCreateCommunityViewModel.createCommunity(requestBody, commImage)
        }
        binding.createCommunityBackButton.setOnClickListener { finish() }
        binding.createCommunityIconSelect.setOnClickListener { pickImageFromGallery() }
    }

    private fun setTextChangeListeners() {
        binding.createCommunityNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkCanGoNext()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.createCommunityAboutInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkCanGoNext()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun redirectToNewCommunity(community: Community?) {
        val intent = Intent(this, CommunityActivity::class.java)
        intent.putExtra("community", community)
        startActivity(intent)
        finish()
    }

    fun checkCanGoNext() {
        val name = binding.createCommunityNameInput.text.toString()
        val about = binding.createCommunityAboutInput.text.toString()
        if (name.isEmpty() || about.isEmpty()) {
            if (isNextAllowed!!) {
                switchNextAllowed()
                fadeOutAnim()
            }
        } else {
            if (!isNextAllowed!!) {
                switchNextAllowed()
                fadeInAnim()
            }
        }
    }

    private fun switchNextAllowed() {
        isNextAllowed = !isNextAllowed!!
        binding.createCommunityNextButton.isClickable = isNextAllowed!!
    }

    private fun fadeInAnim() {
        val fadeInAnim = AlphaAnimation(0.5f, 1.0f)
        fadeInAnim.duration = 600
        fadeInAnim.fillAfter = true
        binding.createCommunityNextButton.startAnimation(fadeInAnim)
    }

    private fun fadeOutAnim() {
        val fadeOutAnim = AlphaAnimation(1.0f, 0.5f)
        fadeOutAnim.duration = 600
        fadeOutAnim.fillAfter = true
        binding.createCommunityNextButton.startAnimation(fadeOutAnim)
    }

    private fun pickImageFromGallery() {
        val gallery = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, ArrayList<String>().apply {
                add("image/jpeg")
                add("image/png")
                add("image/jpg")
            })
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        pickGalleryResultLauncher.launch(gallery)
    }

    private var pickGalleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUri: Uri?
        if (result.data != null) {
            imageUri = result.data!!.data
            try {
                launchImageCrop(imageUri)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                binding.createCommunityIcon.setImageBitmap(bitmap)
            } catch (e: Exception) {
                this.shortToast(resources.getString(R.string.error_try_again_later))
            }
        }
    }

    private var cropImage =
        registerForActivityResult(
            CropImageContract()
        ) { result: CropImageView.CropResult ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    setIcon(it)
                }
            } else {
                result.error?.printStackTrace()
            }
        }

    private fun setIcon(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(binding.createCommunityIcon)
    }

    private fun launchImageCrop(imageUri: Uri?) {
        cropImage.launch(
            CropImageContractOptions(
                imageUri, CropImageOptions(
                    guidelines = Guidelines.ON,
                )
            ),
            ActivityOptionsCompat.makeBasic()
        )
    }

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), leftImageFile)
        return MultipartBody.Part.createFormData(fileName, leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        val file = File(
            applicationContext.filesDir,
            "$fileName.png"
        )
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitMapData = bos.toByteArray()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos!!.write(bitMapData)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}