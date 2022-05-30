package com.example.vknewsviewer.data

import com.example.vknewsviewer.utils.EMPTY_STRING
import com.google.gson.annotations.SerializedName

data class VkUser (
    val id: Int,
    @SerializedName("first_name") val firstName: String = EMPTY_STRING,
    @SerializedName("last_name") val lastName: String = EMPTY_STRING,
    @SerializedName("photo_100") val photoImageUrl: String = EMPTY_STRING
) {

    fun getUserName(): String {
        return "$firstName $lastName"
    }
}