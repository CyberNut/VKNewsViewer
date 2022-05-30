package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class LangResponse(
    @SerializedName("lang") val languageCode: Int
)
