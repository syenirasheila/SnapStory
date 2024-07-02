package com.example.snapstory.ui.storymaps

import androidx.lifecycle.ViewModel
import com.example.snapstory.data.repository.PostStoryRepository

class StoryMapsViewModel (private val postStoryRepository: PostStoryRepository) : ViewModel() {

    fun getStoryLocation() = postStoryRepository.getStoryLocation()
}