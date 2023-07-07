package com.edufelip.finn.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.edufelip.finn.R
import com.edufelip.finn.databinding.ActivityCropImageBinding
import java.io.ByteArrayOutputStream


class CropImageActivity: AppCompatActivity() {

    private var _binding: ActivityCropImageBinding? = null
    private val binding get() = _binding!!
    private var cropImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCropImageBinding.inflate(layoutInflater)
        getExtras()
        setImageUri()
        setCropImageStyle()
        setupClickListeners()
        setContentView(binding.root)
    }

    private fun getExtras() {
        val cropImageUIData = intent.extras?.getString(EXTRA_CROP_IMAGE_DATA)
        cropImageUIData?.let { this.cropImageUri = it }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.cropButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_CROP_IMAGE_RESULT, getByteArrayFromBitmap())
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun getByteArrayFromBitmap(): ByteArray {
        val bitmap = binding.cropImageView.getCroppedImage()
        val bStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, bStream)
        return bStream.toByteArray()
    }

    private fun setCropImageStyle() {
        binding.cropImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.darker_grey))
    }

    private fun setImageUri() {
        binding.cropImageView.setImageUriAsync(Uri.parse(cropImageUri))
    }

    companion object {
        private const val EXTRA_CROP_IMAGE_DATA = "com.edufelip.finn.extra_crop_image_uri"
        const val EXTRA_CROP_IMAGE_RESULT = "com.edufelip.finn.extra_crop_image_result"

        fun getIntentLauncher(context: Context, imageUri: Uri): Intent {
            return Intent(context, CropImageActivity::class.java).apply {
                putExtra(EXTRA_CROP_IMAGE_DATA, imageUri.toString())
            }
        }
    }
}