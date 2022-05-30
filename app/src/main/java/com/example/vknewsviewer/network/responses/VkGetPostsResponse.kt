package com.example.vknewsviewer.network.responses

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUser
import com.example.vknewsviewer.utils.EMPTY_STRING
import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("items") val items: List<Post>,
    @SerializedName("groups") val groups: List<Group>,
    @SerializedName("profiles") val profiles: List<VkUser>,
    @SerializedName("next_from") val nextFrom: String?
)

data class Group(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = EMPTY_STRING,
    @SerializedName("type") val type: String = EMPTY_STRING,
    @SerializedName("photo_100") val photo100: String = EMPTY_STRING
)
