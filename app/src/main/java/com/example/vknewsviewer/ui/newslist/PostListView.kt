package com.example.vknewsviewer.ui.newslist

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.basemvp.MvpView

interface PostListView: MvpView {

    fun showPosts(posts: List<Post>)

    fun showConnectionError()

    fun showEmptyState()

    fun showLoadingState(isLoading: Boolean)

    fun updatePostLikes(id: Int, likes: Int)

    fun updateBottomNavBar(isLikedPostListEmpty: Boolean)

    fun showUserProfile(userId: Int)

    fun showUnsupportedOperationMessage()

}