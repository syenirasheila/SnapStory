package com.example.snapstory.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.snapstory.data.remote.model.RegisterResponse
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.helper.UserResult
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _signupResponse = MutableLiveData<UserResult<RegisterResponse>>()
    val signupResponse: LiveData<UserResult<RegisterResponse>> get() = _signupResponse
    fun signup (name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.signup(name, email, password).asFlow().collect { result ->
                _signupResponse.postValue(result)
            }
        }
    }
}