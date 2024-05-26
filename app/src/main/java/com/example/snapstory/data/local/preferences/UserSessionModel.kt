package com.example.snapstory.data.local.preferences

data class UserSessionModel(
    val email: String,
    val token: String,
    val isSessionActive: Boolean = false
)
