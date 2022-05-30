package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.post.PostActionsCallback
import kotlinx.android.synthetic.main.post_with_image_list_item.view.*
import kotlinx.android.synthetic.main.view_image_post.view.*

class SinglePostViewHolder(
    private val postActionsCallback: PostActionsCallback, root: View
) : RecyclerView.ViewHolder(root) {

    private var isLiked: Boolean = false

    fun bind(mPost: Post) {
        isLiked = mPost.isLiked
        itemView.postWithImageLayout.contentText = mPost.text
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
        itemView.postWithImageLayout.likeButton.setOnClickListener {
            postActionsCallback.onLikePostClicked(mPost)
        }
        itemView.postWithImageLayout.imageView.setOnClickListener {
            if (mPost.imageUrl.isNotEmpty()) {
                postActionsCallback.onImagePostClicked(mPost)
            }
        }
        itemView.postWithImageLayout.shareButton.setOnClickListener {
            if (mPost.imageUrl.isNotEmpty()) {
                postActionsCallback.onSharePostClicked(mPost)
            }
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