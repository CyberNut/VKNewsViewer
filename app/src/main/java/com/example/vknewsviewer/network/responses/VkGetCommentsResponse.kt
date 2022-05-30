package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("can_post") val canPost: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("current_level_count") val currentLevelCount: Int,
    @SerializedName("items") val items: List<CommentItem>?,
    @SerializedName("show_reply_button") val showReplyButton: Boolean
)

data class CommentItem(
    @SerializedName("date") val date: Long,
    @SerializedName("from_id") val fromId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("owner_id") val ownerId: Int,
    @SerializedName("parents_stack") val parentsStack: List<Any>,
    @SerializedName("post_id") val postId: Int,
    @SerializedName("text") val text: String,
    @SerializedName("thread") val thread: Thread
)

data class Thread(
    @SerializedName("can_post") val canPost: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("items") val items: List<Any>,
    @SerializedName("show_reply_button") val showReplyButton: Boolean
)