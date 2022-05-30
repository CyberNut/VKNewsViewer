package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.NewsActionsCallback
import kotlinx.android.synthetic.main.post_with_image_list_item.view.*
import kotlinx.android.synthetic.main.view_image_post.view.*

const val TEXT_MAX_SYMBOLS_IN_PREVIEW = 300

class PostWithImageViewHolder(
    private val newsActionsCallback: NewsActionsCallback,
    root: View
) : RecyclerView.ViewHolder(root) {

    private var isLiked: Boolean = false

    fun bind(mPost: Post) {
        isLiked = mPost.isLiked
        val textToSet = if (mPost.text.length > TEXT_MAX_SYMBOLS_IN_PREVIEW) {
            mPost.text.take(TEXT_MAX_SYMBOLS_IN_PREVIEW) + itemView.context.getString(R.string.read_more)
        } else {
            mPost.text
        }
        itemView.postWithImageLayout.contentText = textToSet
        itemView.postWithImageLayout.titleText = mPost.group
        itemView.postWithImageLayout.dateText = mPost.getDateTimeString()
        itemView.postWithImageLayout.numberOfLikesTextView.isVisible = mPost.numberOfLikes > 0
        itemView.postWithImageLayout.numberOfLikes = mPost.numberOfLikes
        itemView.postWithImageLayout.numberOfCommentsTextView.isVisible = mPost.commentsCount > 0
        itemView.postWithImageLayout.numberOfComments = mPost.commentsCount
        Glide.with(itemView.context).load(mPost.imageUrl)
            .into(itemView.postWithImageLayout.imageView)
        Glide.with(itemView.context).load(mPost.logoImageUrl)
            .into(itemView.postWithImageLayout.logoImageView)
        itemView.setOnClickListener {
            newsActionsCallback.onPostClicked(mPost.ownerId, mPost.id)
        }
        itemView.postWithImageLayout.likeButton.setOnClickListener {
            newsActionsCallback.onPostLiked(mPost.ownerId, mPost.id)
        }
        itemView.postWithImageLayout.logoImageView.setOnClickListener {
            newsActionsCallback.onPostLogoClicked(mPost.sourceId)
        }
        updateLikes(mPost.isLiked, mPost.numberOfLikes)
    }

    fun like(likes: Int) {
        isLiked = !isLiked
        updateLikes(isLiked, likes)
    }

    private fun updateLikes(isLiked: Boolean, likes: Int) {
        if (isLiked) {
            itemView.postWithImageLayout.likeButton.setImageResource(R.drawable.ic_like_icon_pressed)
        } else {
            itemView.postWithImageLayout.likeButton.setImageResource(R.drawable.ic_like_icon)
        }
        itemView.postWithImageLayout.numberOfLikes = likes
        itemView.postWithImageLayout.numberOfLikesTextView.isVisible = likes > 0
    }
}