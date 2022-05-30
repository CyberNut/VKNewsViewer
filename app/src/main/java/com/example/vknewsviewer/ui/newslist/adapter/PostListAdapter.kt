package com.example.vknewsviewer.ui.newslist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.viewholders.PostWithImageViewHolder
import com.example.vknewsviewer.utils.DAY_IN_MILLIS
import com.example.vknewsviewer.utils.getStartDay
import kotlin.math.abs

open class PostListAdapter(protected val newsActionsCallback: NewsActionsCallback) :
    ListAdapter<Post, PostWithImageViewHolder>(PostDiffCallback()), ItemSwipeHelper, DecorationTypeProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWithImageViewHolder {
        return PostWithImageViewHolder(
            newsActionsCallback,
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_with_image_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostWithImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onRightSwipe(position: Int) {
        val newList = ArrayList<Post>(currentList)
        newList.removeAt(position)
        submitList(newList)
    }

    override fun onLeftSwipe(position: Int) {
        val post = getItem(position)
        post?.let {
            newsActionsCallback.onPostLiked(post.ownerId, post.id)
        }
    }

    override fun getType(position: Int): DecorationType {
        if(position == RecyclerView.NO_POSITION) {
            return DecorationType.SimpleDivider
        }
        if (itemCount == 0) {
            return DecorationType.SimpleDivider
        }
        if (position == 0) {
            return DecorationType.DividerWithText(getItem(0).date)
        }
        val previousItem = getItem(position - 1)
        val currentItem = getItem((position))

        return if (abs(getStartDay(previousItem.date) - getStartDay(currentItem.date)) >= DAY_IN_MILLIS) {
            DecorationType.DividerWithText(getItem(position).date)
        } else {
            DecorationType.SimpleDivider
        }
    }

    fun updateLikedPost(id: Int, likes: Int) {
        val newList = mutableListOf<Post>()
        newList.addAll(currentList)
        val index = newList.indexOfFirst { post -> post.id == id }
        if (index >= 0) {
            val post = newList[index].copy()
            post.like(likes)
            newList[index] = post
        }
        submitList(newList)
    }
}