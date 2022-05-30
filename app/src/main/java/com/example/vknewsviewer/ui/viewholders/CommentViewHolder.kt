package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vknewsviewer.data.Comment
import kotlinx.android.synthetic.main.comment_list_item.view.*

class CommentViewHolder(private val showUserProfileFunc: (Int) -> Unit, root: View) : RecyclerView.ViewHolder(root) {

    private var userId: Int = 0

    fun bind(comment: Comment) {
        comment.user?.let { vkUser ->
            userId = vkUser.id
        }
        itemView.userNameTextView.text = comment.user?.getUserName()
        itemView.commentTextView.text = comment.text
        itemView.commentDateTextView.text = comment.getDateTimeString()
        Glide.with(itemView.context).load(comment.user?.photoImageUrl)
            .into(itemView.avatarImageView)
        itemView.avatarImageView.setOnClickListener {
            showUserProfileFunc(userId)
        }
    }
}
