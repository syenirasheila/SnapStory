package com.example.snapstory.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.repository.AuthRepository
import com.example.snapstory.helper.UserResult
import kotlinx.coroutines.launch

class SigninViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _signinResponse = MutableLiveData<UserResult<LoginResponse>>()
    val signinResponse: LiveData<UserResult<LoginResponse>> get() = _signinResponse
    fun signin ( email: String, password: String) {
        viewModelScope.launch {
            authRepository.signin(email, password).asFlow().collect { result ->
                _signinResponse.postValue(result)
            }
        }
    }
}