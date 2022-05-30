package com.example.vknewsviewer.network.responses

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.repository.PostsRepository.Companion.ATTACHMENT_TYPE_LINK
import com.example.vknewsviewer.repository.PostsRepository.Companion.ATTACHMENT_TYPE_PHOTO
import com.example.vknewsviewer.repository.PostsRepository.Companion.ATTACHMENT_TYPE_VIDEO
import com.example.vknewsviewer.utils.EMPTY_STRING
import com.example.vknewsviewer.utils.toBoolean
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

const val INVALID_ID = -1

class VkPostDeserializer : JsonDeserializer<Post> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Post {

        if (json == null) {
            return Post(INVALID_ID)
        }

        val postResponse = json.asJsonObject
        val id = postResponse.get("post_id").asInt
        val sourceId = postResponse.get("source_id").asInt
        val date = postResponse.get("date").asLong * 1000L
        val likeInfo = postResponse.get("likes").asJsonObject
        val likesCount = likeInfo.get("count").asInt
        val userLikes = likeInfo.get("user_likes").asInt.toBoolean()
        val commentsCount = postResponse.get("comments").asJsonObject.get("count").asInt
        val canComment = postResponse.get("comments").asJsonObject.get("can_post").asInt
        val text = postResponse.get("text").asString
        val imageUrl = getItemImageUrl(postResponse)

        return Post(
            id,
            sourceId = sourceId,
            ownerId = sourceId,
            date = date,
            numberOfLikes = likesCount,
            isLiked = userLikes,
            commentsCount = commentsCount,
            canComment = canComment.toBoolean(),
            text = text,
            imageUrl = imageUrl
        )
    }

    private fun getItemImageUrl(item: JsonObject): String {
        val attachments = item.get("attachments")?.asJsonArray
        attachments?.map { attachment ->
            return when (attachment.asJsonObject.get("type").asString) {
                ATTACHMENT_TYPE_PHOTO -> {
                    attachment.asJsonObject.get("photo").asJsonObject.get("sizes").asJsonArray.last().asJsonObject.get(
                        "url"
                    ).asString
                }
                ATTACHMENT_TYPE_VIDEO -> {
                    return when {
                        attachment.asJsonObject.get("video")?.asJsonObject?.get("image") != null -> {
                            attachment.asJsonObject.get("video").asJsonObject.get("image").asJsonArray.last().asJsonObject.get(
                                "url"
                            ).asString
                        }
                        attachment.asJsonObject.get("video").asJsonObject.get("first_frame") != null -> {
                            attachment.asJsonObject.get("video").asJsonObject.get("first_frame").asJsonArray.last().asJsonObject.get(
                                "url"
                            ).asString
                        }
                        else -> EMPTY_STRING
                    }
                }
                ATTACHMENT_TYPE_LINK -> {
                    attachment.asJsonObject?.get("link")?.asJsonObject?.get("photo")?.asJsonObject?.get(
                        "sizes"
                    )?.asJsonArray?.last()?.asJsonObject?.get(
                        "url"
                    )?.asString ?: EMPTY_STRING
                }
                else -> EMPTY_STRING
            }
        }
        return EMPTY_STRING
    }
}