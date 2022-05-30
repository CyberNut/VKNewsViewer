package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import kotlinx.android.synthetic.main.error_loading_item.view.*

class ErrorLoadingViewHolder(root: View) :
    RecyclerView.ViewHolder(root) {

    fun bind(message: String) {
        if (message.isNotEmpty()) {
            itemView.errorMessageTextView.text = message
        } else {
            itemView.errorMessageTextView.text =
                itemView.context.getString(R.string.unexpected_error)
        }
    }
}