package com.example.snapstory.data.remote.api

import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.remote.model.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun signin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse


}