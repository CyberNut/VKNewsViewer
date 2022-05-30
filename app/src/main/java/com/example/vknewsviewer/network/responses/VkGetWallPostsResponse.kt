package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class WallPostResponse (
    @SerializedName("count") val count : Int,
    @SerializedName("items") val items : List<WallItem>?
)

data class WallItem (
    @SerializedName("id") val id : Int,
    @SerializedName("from_id") val from_id : Int,
    @SerializedName("owner_id") val owner_id : Int,
    @SerializedName("date") val date : Long,
    @SerializedName("post_type") val post_type : String,
    @SerializedName("attachments") val attachments: List<WallAttachment>?,
    @SerializedName("text") val text : String,
    @SerializedName("comments") val comments : CommentsInfo,
    @SerializedName("likes") val likes : Likes,
    @SerializedName("is_favorite") val is_favorite : Boolean
)

data class WallAttachment(
    @SerializedName("type") val type: String,
    @SerializedName("link") val link: WallLink?,
    @SerializedName("photo") val photos: WallPhotos?,
    @SerializedName("video") val video: WallVideo?
)

data class WallPhotos(
    @SerializedName("sizes") val photos: List<Photo>
)

data class WallVideo(
    @SerializedName("image") val images: List<Photo>
)

data class WallLink(
    @SerializedName("url") val url: String?,
    @SerializedName("photo") val photo: Photo?
)

data class CommentsInfo(
    @SerializedName("count") val count: Int,
    @SerializedName("can_post") val canPost: Int
)

data class Photo(
    @SerializedName("url") val url: String
)

data class Likes(
    @SerializedName("count") val count: Int,
    @SerializedName("user_likes") val userLikes: Int
)