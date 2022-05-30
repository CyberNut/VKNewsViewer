package com.example.vknewsviewer.network.responses

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUser
import com.example.vknewsviewer.utils.EMPTY_STRING
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import kotlin.math.abs

class VkPostResponseDeserializer : JsonDeserializer<PostResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PostResponse {

        if (json == null || context == null) {
            return PostResponse(emptyList(), emptyList(), emptyList(), null)
        }

        val postResponse = json.asJsonObject

        val nextFrom = postResponse?.get("next_from")?.asString

        val groups = mutableMapOf<Int, Group>()
        postResponse.get("groups").asJsonArray.forEach { groupJson ->
            val group: Group = context.deserialize(groupJson, Group::class.java)
            groups[group.id] = group
        }

        val profiles = mutableMapOf<Int, VkUser>()
        postResponse.get("profiles").asJsonArray.forEach { profileJson ->
            val profile: VkUser = context.deserialize(profileJson, VkUser::class.java)
            profiles[profile.id] = profile
        }

        val posts = mutableListOf<Post>()
        postResponse.get("items").asJsonArray.forEach { itemJson ->
            val post: Post = context.deserialize(itemJson, Post::class.java)
            if (post.sourceId > 0) {
                val user = profiles[post.sourceId]
                post.group = user?.getUserName() ?: EMPTY_STRING
                post.logoImageUrl = user?.photoImageUrl ?: EMPTY_STRING
            } else {
                val group = groups[abs(post.sourceId)]
                post.group = group?.name ?: EMPTY_STRING
                post.logoImageUrl = group?.photo100 ?: EMPTY_STRING
            }
            if (post.imageUrl.isNotEmpty() || post.text.isNotEmpty()) {
                posts.add(post)
            }
        }

        return PostResponse(posts, groups.values.toList(), profiles.values.toList(), nextFrom)
    }
}