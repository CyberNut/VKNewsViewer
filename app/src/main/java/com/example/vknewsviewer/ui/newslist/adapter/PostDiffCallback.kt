package com.example.vknewsviewer.ui.newslist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vknewsviewer.data.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

//  payloads doesn't work with my swipes
//    override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
//        return newItem.numberOfLikes
//    }
}