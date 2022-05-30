package com.example.vknewsviewer.ui.favoriteslist

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.newslist.adapter.PostListAdapter

class FavoritesPostListAdapter(
    newsActionsCallback: NewsActionsCallback
) : PostListAdapter(newsActionsCallback) {

    override fun onRightSwipe(position: Int) {
        deleteLikedPost(position)
    }

    override fun onLeftSwipe(position: Int) {
        deleteLikedPost(position)
    }

    private fun deleteLikedPost(position: Int) {
        val post = getItem(position)
        post?.let {
            val newList = ArrayList<Post>(currentList)
            newList.removeAt(position)
            submitList(newList)
            newsActionsCallback.onPostLiked(post.ownerId, post.id)
        }
    }
}