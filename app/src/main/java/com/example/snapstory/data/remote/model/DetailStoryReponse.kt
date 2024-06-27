package com.example.snapstory.data.remote.model

import com.google.gson.annotations.SerializedName

data class DetailResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("story")
    val story: StoryItem
)