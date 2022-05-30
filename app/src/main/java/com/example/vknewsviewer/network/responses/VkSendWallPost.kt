package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

class SendWallPost(
    @SerializedName("post_id") val postId: Int
)