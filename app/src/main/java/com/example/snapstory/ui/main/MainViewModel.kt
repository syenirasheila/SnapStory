package com.example.snapstory.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.snapstory.data.local.preferences.UserSessionModel
import com.example.snapstory.data.remote.model.StoryItem
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.data.repository.PostStoryRepository
import kotlinx.coroutines.launch

class MainViewModel (private val authRepository: AuthRepository, postStoryRepository: PostStoryRepository) : ViewModel()  {


    private val dataRefreshTrigger = MutableLiveData<Unit>()
    init {
        refreshData()
    }
    fun refreshData() {
        dataRefreshTrigger.value = Unit
    }

    val storyList: LiveData<PagingData<StoryItem>> = dataRefreshTrigger.switchMap {
        postStoryRepository.getAllStories().cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getUserSession(): LiveData<UserSessionModel> {
        return authRepository.getUserSession().asLiveData()
    }
}