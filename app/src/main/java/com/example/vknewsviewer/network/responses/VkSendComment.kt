package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class SendComment(
    @SerializedName("comment_id") val commentId: Int
)