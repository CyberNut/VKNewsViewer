package com.example.vknewsviewer.ui.userprofile

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUserProfile
import com.example.vknewsviewer.ui.basemvp.MvpView

interface UserProfileView: MvpView {

    fun showUserProfile(userProfile: VkUserProfile)

    fun showUserWall(posts: List<Post>)

    fun showErrorMessage(message: String, onlyToast: Boolean)

    fun updatePostLikes(postId: Int, likesCount: Int)

    fun openUserProfile(userId: Int)

    fun showPostLoadingProgress(isLoading: Boolean)

    fun showProfileLoadingProgress(isLoading: Boolean)

    fun onSendingCommentError()

    fun onSendingCommentSuccess()

    fun showUnsupportedOperationMessage()

    fun showOpenProfileError()
}