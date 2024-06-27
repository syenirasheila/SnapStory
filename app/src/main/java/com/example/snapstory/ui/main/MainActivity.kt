package com.example.snapstory.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapstory.data.remote.model.StoryItem
import com.example.snapstory.databinding.ActivityMainBinding
import com.example.snapstory.helper.ViewModelFactory
import com.example.snapstory.ui.addimage.AddImageActivity
import com.example.snapstory.ui.addimage.AddImageActivity.Companion.CAMERAX_RESULT
import com.example.snapstory.ui.loading.LoadingStateAdapter
import com.example.snapstory.ui.onboarding.OnboardingActivity
import com.example.snapstory.ui.storydetail.StoryDetailActivity
import com.example.snapstory.ui.storydetail.StoryDetailActivity.Companion.EXTRA_ID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListAdapter : StoryListAdapter
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.getUserSession().observe(this) {
            if (!it.isSessionActive) {
                navigateToOnboarding()
                finish()
            }
            initializeUI()
            initializeRefresh()
            getStoryRecycle()
            onClickCallback()
        }
    }

    private fun initializeUI() {
        storyListAdapter = StoryListAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            setHasFixedSize(true)
            adapter = storyListAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyListAdapter.retry() }
            )
        }

        mainViewModel.storyList.observe(this) {
            storyListAdapter.submitData(lifecycle, it)
        }

        storyListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading) {
                binding.swipeToRefresh.isRefreshing = false
            } else if (loadState.refresh is LoadState.Loading) {
                binding.swipeToRefresh.isRefreshing = true
            }
        }

        binding.btnLogout.setOnClickListener{
            mainViewModel.logout()
            navigateToOnboarding()
        }

        binding.btnAddStory.setOnClickListener {
            openCamera()
        }

    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun initializeRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            mainViewModel.refreshData()
            binding.swipeToRefresh.isRefreshing = false
            Log.d("Refresh MainActivity", "Refresh")
        }
    }
    private fun onClickCallback() {
        storyListAdapter.setOnItemCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryItem) {
                val intent = Intent(this@MainActivity, StoryDetailActivity::class.java).apply {
                    putExtra(EXTRA_ID, data.id)
                }
                startActivity(intent)
            }
        })
    }

    private fun openCamera() {
        val intent = Intent(this, AddImageActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(AddImageActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
        }
    }

    private fun getStoryRecycle() {
        storyListAdapter = StoryListAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = storyListAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyListAdapter.retry()
                }
            )
        }
        mainViewModel.storyList.observe(this) {
            storyListAdapter.submitData(lifecycle, it)
        }
        storyListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading) {
                binding.swipeToRefresh.isRefreshing = false
            } else if (loadState.refresh is LoadState.Loading) {
                binding.swipeToRefresh.isRefreshing = true
            }
        }
    }
}