package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName("likes") val likes: Int
)