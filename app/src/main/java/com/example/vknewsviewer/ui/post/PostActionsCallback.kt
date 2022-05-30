package com.example.vknewsviewer.ui.post

import com.example.vknewsviewer.data.Post

interface PostActionsCallback {

    fun onLikePostClicked(post: Post)

    fun onReloadCommentsClicked()

    fun onSharePostClicked(post: Post)

    fun onImagePostClicked(post: Post)
}