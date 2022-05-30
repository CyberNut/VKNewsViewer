package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.error_comments_loading_item.view.*

class ErrorCommentsLoadingViewHolder(private val reloadCommentsFun: () -> Unit, root: View) :
    RecyclerView.ViewHolder(root) {

    init {
        itemView.reloadCommentsButton.setOnClickListener {
            reloadCommentsFun()
        }
    }
}