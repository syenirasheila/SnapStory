package com.example.snapstory.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.snapstory.data.local.preferences.UserSessionModel
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.remote.api.ApiService
import com.example.snapstory.data.remote.model.ErrorResponse
import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.remote.model.RegisterResponse
import com.example.snapstory.helper.UserResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

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
        }.also { instance = it }
    }

    fun signup (name: String, email: String, password: String): LiveData<UserResult<RegisterResponse>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val response = apiService.signup(name, email, password)
                emit(UserResult.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                emit(UserResult.Error(errorResponse.message.toString()))
            } catch (e: Exception) {
                emit(UserResult.Error(e.message.toString()))
            }
        }
    }

    fun signin (email: String, password: String): LiveData<UserResult<LoginResponse>> {
        return liveData(Dispatchers.IO) {
            emit(UserResult.Loading)
            try {
                val response = apiService.signin(email, password)
                response.loginResult?.let {
                    userSessionPreference.saveUserSession(UserSessionModel(email, it.token, true))
                }
                emit(UserResult.Success(response))
            } catch (e: Exception) {
                emit(UserResult.Error(e.message.toString()))
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