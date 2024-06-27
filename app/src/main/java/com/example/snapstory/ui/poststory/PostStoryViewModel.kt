package com.example.snapstory.ui.poststory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.snapstory.data.remote.model.PostStoryResponse
import com.example.snapstory.data.repository.PostStoryRepository
import com.example.snapstory.helper.UserResult
import java.io.File

class PostStoryViewModel(private val postStoryRepository: PostStoryRepository) : ViewModel() {

    fun addImage(image: File, description: String): LiveData<UserResult<PostStoryResponse>> {
        return postStoryRepository.postStory(image, description)
    }
}