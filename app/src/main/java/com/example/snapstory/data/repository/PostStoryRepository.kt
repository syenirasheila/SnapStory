package com.example.snapstory.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.snapstory.data.local.db.SnapStoryDb
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.remote.api.ApiService
import com.example.snapstory.data.remote.model.PostStoryResponse
import com.example.snapstory.data.remote.model.StoryItem
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.handleError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostStoryRepository private constructor(
    private val snapStoryDatabase: SnapStoryDb,
    private val apiService: ApiService,
    private val userSessionPreference: UserSessionPreference
) {
    companion object {
        @Volatile
        private var instance: PostStoryRepository? = null
        fun getInstance(
            snapStoryDatabase: SnapStoryDb,
            apiService: ApiService,
            userSessionPreference: UserSessionPreference
        ): PostStoryRepository =
            instance ?: synchronized(this) {
                instance ?: PostStoryRepository(snapStoryDatabase, apiService, userSessionPreference)
            }.also {
                instance = it }
    }

    fun getAllStories(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(snapStoryDatabase, userSessionPreference, apiService),
            pagingSourceFactory = {
                snapStoryDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getStoryDetail(id: String): LiveData<UserResult<StoryItem>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val token = userSessionPreference.getUserToken().first()
                val response = apiService.detailStory("Bearer $token", id)
                val result = response.story
                emit(UserResult.Success(result))
            } catch (e: Exception) {
                val errorMessage = handleError(e)
                emit(UserResult.Error(errorMessage))
            }
        }
    }

    fun postStory(image: File, description: String, lat: Double?, lon: Double?): LiveData<UserResult<PostStoryResponse>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val token = userSessionPreference.getUserToken().first()
                val requestDescription = description.toRequestBody("text/plain".toMediaType())
                val requestImage = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    image.name,
                    requestImage
                )
                val latPart = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonPart = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                val response = if (latPart != null && lonPart != null) {
                    apiService.postStory("Bearer $token", imageMultipart, requestDescription, latPart, lonPart)
                } else {
                    apiService.postStory("Bearer $token", imageMultipart, requestDescription)
                }
                emit(UserResult.Success(response))
            } catch (e: Exception) {
                val errorMessage = handleError(e)
                emit(UserResult.Error(errorMessage))
            }
        }
    }

    fun getStoryLocation(): LiveData<UserResult<List<StoryItem>>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val token = userSessionPreference.getUserToken().first()
                val response = apiService.getAllStory("Bearer $token", location = 1)
                val result = response.listStory
                emit(UserResult.Success(result))
            } catch (e: Exception) {
                val errorMessage = handleError(e)
                emit(UserResult.Error(errorMessage))
            }
        }
    }
}