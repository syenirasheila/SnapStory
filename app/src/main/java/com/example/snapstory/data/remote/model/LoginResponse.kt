package com.example.snapstory.data.remote.model

data class LoginResponse(

    val loginResult: LoginResult,
    val error: Boolean,
    val message: String
)

