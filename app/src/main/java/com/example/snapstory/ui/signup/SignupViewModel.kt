package com.example.snapstory.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.snapstory.data.remote.model.RegisterResponse
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.helper.UserResult

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun signup (name: String, email: String, password: String): LiveData<UserResult<RegisterResponse>> {
        return authRepository.signup(name, email, password)
    }
}