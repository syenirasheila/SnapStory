package com.example.snapstory.data.remote.api

import com.example.snapstory.data.remote.model.DetailResponse
import com.example.snapstory.data.remote.model.PostStoryResponse
import com.example.snapstory.data.remote.model.LoginResponse
import com.example.snapstory.data.remote.model.RegisterResponse
import com.example.snapstory.data.remote.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("location") location: Int = 0
    ) : StoryResponse

    @GET("stories/{id}")
    suspend fun detailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : DetailResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): PostStoryResponse

}