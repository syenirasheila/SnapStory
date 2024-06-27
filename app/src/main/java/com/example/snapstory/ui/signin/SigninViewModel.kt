package com.example.snapstory.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.helper.UserResult

class SigninViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun signin(email: String, password: String): LiveData<UserResult<LoginResponse>> {
        return authRepository.signin(email, password)
    }
}