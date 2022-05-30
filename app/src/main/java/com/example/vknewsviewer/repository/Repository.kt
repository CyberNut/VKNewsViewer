package com.example.vknewsviewer.repository

import com.example.vknewsviewer.data.Comment
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUserProfile
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {
    fun getPosts(forceUpdate: Boolean = false): Observable<List<Post>>
    fun getLikedPosts(): Single<List<Post>>
    fun getPost(ownerId: Int, postId: Int): Single<Post>
    fun likePost(ownerId: Int, postId: Int): Maybe<Pair<Int, Int>>
    fun getComments(post: Post): Single<List<Comment>>
    fun deletePost(id: Int): Completable
    fun sendComment(post: Post, commentText: String): Completable
    fun getWallPosts(userId: Int, usingCache: Boolean): Observable<List<Post>>
    fun sendWallPost(userId: Int, commentText: String): Completable
    fun getUserProfile(
        userId: Int,
        usingCache: Boolean,
        forceUpdate: Boolean
    ): Observable<VkUserProfile>
}
