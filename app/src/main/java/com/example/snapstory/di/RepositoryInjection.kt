package com.example.snapstory.di

import android.content.Context
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.local.preferences.dataStore
import com.example.snapstory.data.remote.api.ApiConfig
import com.example.snapstory.data.repository.AuthRepository

object RepositoryInjection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserSessionPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }
}