package com.example.vknewsviewer.ui

interface NewsActionsCallback {
    fun onPostClicked(ownerId: Int, postId: Int)
    fun onPostLiked(ownerId: Int, postId: Int)
    fun onPostDeleted(id: Int)
    fun onPostLogoClicked(id: Int)
}
