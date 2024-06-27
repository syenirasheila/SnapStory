package com.example.snapstory.helper

import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun handleError(exception: Exception): String {
    return when (exception) {
        is HttpException -> {
            val errorJsonString = exception.response()?.errorBody()?.string()
            val errorMessage = if (!errorJsonString.isNullOrEmpty()) {
                try {
                    val jsonObject = JSONObject(errorJsonString)
                    jsonObject.getString("message")
                } catch (e: JSONException) {
                    "Unknown error"
                }
            } else {
                "Network error: ${exception.message()}"
            }
            errorMessage
        }
        is IOException -> {
            "Network error: ${exception.message}"
        }
        else -> {
            "Unexpected error: ${exception.message}"
        }
    }
}