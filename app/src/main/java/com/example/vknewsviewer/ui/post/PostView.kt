package com.example.vknewsviewer.ui.post

import com.example.vknewsviewer.data.Comment
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.basemvp.MvpView

interface PostView: MvpView {

    fun showPost(post: Post)

    fun updatePostLikes(likes: Int)

    fun updateBottomNavBar(isLikedPostListEmpty: Boolean)

    fun showProgressBar()

    fun updateComments(comments: List<Comment>)

    fun showCommentsLoadingError()

    fun showLikePostError()

    fun onSendingCommentError(message: String)

    fun onSendingCommentSuccess()

}