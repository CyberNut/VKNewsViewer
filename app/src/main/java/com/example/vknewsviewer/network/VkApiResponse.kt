package com.example.vknewsviewer.network

import com.google.gson.annotations.SerializedName

data class VkApiResponse<T> (
    @SerializedName("response") val response: T,
    @SerializedName("error") val error: VkError?
) {
    fun hasError(): Boolean = error != null
}

data class VkError(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_msg") val errorMessage: String
)