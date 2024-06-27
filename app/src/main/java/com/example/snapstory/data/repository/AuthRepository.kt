package com.example.snapstory.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.snapstory.data.local.preferences.UserSessionModel
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.remote.api.ApiService
import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.remote.model.RegisterResponse
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.handleError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userSessionPreference: UserSessionPreference
) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            userSessionPreference: UserSessionPreference
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, userSessionPreference)
        }.also {
            instance = it }
    }

    fun signup (name: String, email: String, password: String): LiveData<UserResult<RegisterResponse>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val response = apiService
                    .signup(name, email, password)
                if (response.error) {
                    emit(UserResult.Error(response.message))
                } else {
                    emit(UserResult.Success(response))
                }
            } catch (e: Exception) {
                val errorMessage = handleError(e)
                emit(UserResult.Error(errorMessage))
            }
        }
    }

    fun signin (email: String, password: String): LiveData<UserResult<LoginResponse>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val response = apiService.signin(email, password)
                if (response.error) {
                    emit(UserResult.Error(response.message))
                } else {
                    userSessionPreference.saveUserSession(
                        UserSessionModel(
                            email,
                            response.loginResult.token,
                            true
                        )
                    )
                    emit(UserResult.Success(response))
                }
            } catch (e: Exception) {
                val errorMessage = handleError(e)
                emit(UserResult.Error(errorMessage))
            }
        }
    }

    fun getUserSession(): Flow<UserSessionModel> {
        return userSessionPreference.getUserSession()
    }

    suspend fun logout() {
        userSessionPreference.clearUserSession()
    }

}