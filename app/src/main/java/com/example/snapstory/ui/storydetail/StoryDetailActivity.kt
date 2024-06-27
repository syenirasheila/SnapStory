package com.example.snapstory.ui.storydetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.snapstory.databinding.ActivityStoryDetailBinding
import com.example.snapstory.helper.ViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snapstory.R
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.formatToString

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private val storyDetailViewModel by viewModels<StoryDetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_ID)
        storyId?.run(storyDetailViewModel::setStoryId)

        setSupportActionBar(binding.storyDetailToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storyDetailViewModel.storyDetail.observe(this) {
            when (it) {
                is UserResult.Success -> {
                    showLoading(false)
                    val item = it.data
                    binding.apply {
                        Glide.with(this@StoryDetailActivity)
                            .load(item.photoUrl)
                            .into(ivPreviewStory)
                        storyDetailToolbar.title = item.name
                        storyDetailToolbar.subtitle = item.createdAt?.formatToString()
                        tvStoryDescription.text = item.description
                    }
                }

                is UserResult.Loading -> {
                    showLoading(true)
                }

                is UserResult.Error -> {
                    showLoading(false)
                    showToast(getString(R.string.storyDetail_failed))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}