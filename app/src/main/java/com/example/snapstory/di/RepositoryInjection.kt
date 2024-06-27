package com.example.snapstory.di

import android.content.Context
import com.example.snapstory.data.local.db.SnapStoryDb
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.local.preferences.dataStore
import com.example.snapstory.data.remote.api.ApiConfig
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.data.repository.PostStoryRepository

object RepositoryInjection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserSessionPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }
    fun providePostStoryRepository(context: Context): PostStoryRepository {
        val pref = UserSessionPreference.getInstance(context.dataStore)
        val database = SnapStoryDb.getDb(context)
        val apiService = ApiConfig.getApiService()
        return PostStoryRepository.getInstance(database, apiService, pref)
    }
}