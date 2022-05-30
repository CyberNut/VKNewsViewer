package com.example.vknewsviewer.network

import com.example.vknewsviewer.network.responses.CommentResponse
import com.example.vknewsviewer.network.responses.LangResponse
import com.example.vknewsviewer.network.responses.LikeResponse
import com.example.vknewsviewer.network.responses.PostResponse
import com.example.vknewsviewer.network.responses.SendComment
import com.example.vknewsviewer.network.responses.SendWallPost
import com.example.vknewsviewer.network.responses.UserDataResponse
import com.example.vknewsviewer.network.responses.UserResponse
import com.example.vknewsviewer.network.responses.WallItem
import com.example.vknewsviewer.network.responses.WallPostResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val BASE_URL = "https://api.vk.com/method/"
const val SHARE_URL_PREFIX = "https://api.vk.com/wall"
const val VK_API_VERSION = "5.124"
const val PAGE_SIZE = 15

interface VkApiService {

    @GET("newsfeed.get?filters=post")
    fun getPosts(): Call<VkApiResponse<PostResponse>>

    @GET("newsfeed.get?filters=post")
    fun getNextPosts(
        @Query("start_from") startFrom: String,
        @Query("count") pageSize: Int = PAGE_SIZE
    ): Call<VkApiResponse<PostResponse>>

    @POST("likes.add?type=post")
    fun likePost(
        @Query("item_id") postId: Int,
        @Query("owner_id") ownerId: Int
    ): Call<VkApiResponse<LikeResponse>>

    @POST("likes.delete?type=post")
    fun unlikePost(
        @Query("item_id") postId: Int,
        @Query("owner_id") ownerId: Int
    ): Call<VkApiResponse<LikeResponse>>

    @GET("wall.getComments")
    fun getComments(
        @Query("post_id") postId: Int,
        @Query("owner_id") ownerId: Int
    ): Call<VkApiResponse<CommentResponse>>

    @GET("users.get?fields=photo_200")
    fun getUsers(
        @Query("user_ids") userIds: String
    ): Call<VkApiResponse<List<UserResponse>>>

    @GET("users.get?fields=domain, first_name, last_name, photo, about, online, bdate, city, country, career, education, followers_count, last_seen, photo_100")
    fun getUserProfileData(
        @Query("user_id") userId: String
    ): Call<VkApiResponse<List<UserDataResponse>>>

    @POST("wall.post")
    fun sendWallPost(
        @Query("owner_id") ownerId: Int,
        @Query("message") commentText: String
    ): Call<VkApiResponse<SendWallPost>>

    @POST("wall.createComment")
    fun sendComment(
        @Query("owner_id") ownerId: Int,
        @Query("post_id") postId: Int,
        @Query("message") commentText: String
    ): Call<VkApiResponse<SendComment>>

    @GET("wall.get?filters=all")
    fun getWallPosts(
        @Query("owner_id") userId: Int
    ): Call<VkApiResponse<WallPostResponse>>

    @GET("wall.getById")
    fun getWallPostByIds(
        @Query("posts") queryString: String
    ): Call<VkApiResponse<List<WallItem>?>>


    @GET("account.getInfo?&fields=lang")
    fun getAccountLanguageCode(): Single<VkApiResponse<LangResponse>>
}