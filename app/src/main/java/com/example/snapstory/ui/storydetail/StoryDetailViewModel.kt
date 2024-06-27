package com.example.snapstory.ui.storydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.snapstory.data.remote.model.StoryItem
import com.example.snapstory.data.repository.PostStoryRepository
import com.example.snapstory.helper.UserResult

class StoryDetailViewModel(private val postStoryRepository: PostStoryRepository) : ViewModel() {

    private val storyId = MutableLiveData<String>()

    fun setStoryId (id: String) {
        storyId.value = id
    }
    val storyDetail = storyId.switchMap {
        postStoryRepository.getStoryDetail(it)
    }
}
