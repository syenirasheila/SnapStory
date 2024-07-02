package com.example.snapstory.ui.poststory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snapstory.R
import com.example.snapstory.databinding.ActivityPostStoryBinding
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.ViewModelFactory
import com.example.snapstory.helper.reduceFileImage
import com.example.snapstory.helper.uriToFile
import com.example.snapstory.ui.addimage.AddImageActivity
import com.example.snapstory.ui.main.MainActivity

class PostStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostStoryBinding
    private val postStoryViewModel by viewModels<PostStoryViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var photoUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val photoUriString = intent.getStringExtra(AddImageActivity.EXTRA_CAMERAX_IMAGE)
        photoUri = photoUriString?.let { Uri.parse(it) }

        latitude = intent.getDoubleExtra(AddImageActivity.EXTRA_LOCATION_LAT, Double.NaN)
        longitude = intent.getDoubleExtra(AddImageActivity.EXTRA_LOCATION_LON, Double.NaN)


        Glide.with(this)
            .load(photoUri)
            .into(binding.ivPostStory)

        with(binding) {
            btnClose.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnPostStory.setOnClickListener {
                postStory()
            }
        }
    }

    private fun postStory() {
        photoUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.inputDescription.text.toString().trim()
            Log.d("Image File", "Show Image: ${imageFile.path}")

            val lat = if (latitude != null && !latitude!!.isNaN()) latitude else null
            val lon = if (longitude != null && !longitude!!.isNaN()) longitude else null

            postStoryViewModel.addImage(imageFile, description,lat, lon).observe(this) {
                when (it) {
                    is UserResult.Success<*> -> {
                        showLoading(false)
                        showToast(getString(R.string.postStory_success))
                        navigateToMainActivity()
                    }

                    is UserResult.Loading -> {
                        showLoading(true)
                    }

                    is UserResult.Error -> {
                        showLoading(false)
                        showToast(getString(R.string.postStory_failed))
                    }
                }
            }

        } ?: showToast(getString(R.string.image_empty))
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}