package com.example.vknewsviewer.ui.post

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Comment
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.viewholders.CommentViewHolder
import com.example.vknewsviewer.ui.viewholders.ErrorCommentsLoadingViewHolder
import com.example.vknewsviewer.ui.viewholders.LoadingFooterViewHolder
import com.example.vknewsviewer.ui.viewholders.PostFooterViewHolder
import com.example.vknewsviewer.ui.viewholders.SinglePostViewHolder

class PostAndCommentsAdapter(
    private val postActionsCallback: PostActionsCallback,
    private val showUserProfileFunc: (Int) -> Unit
) :
    ListAdapter<CommentAdapterItem, RecyclerView.ViewHolder>(CommentDiffCallback()) {

    private val TAG = "CommentAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.post_with_image_list_item -> SinglePostViewHolder(postActionsCallback, view)
            R.layout.comment_list_item -> CommentViewHolder(showUserProfileFunc, view)
            R.layout.loading_footer_item -> LoadingFooterViewHolder(view)
            R.layout.comment_footer_item -> PostFooterViewHolder(view)
            R.layout.error_comments_loading_item -> ErrorCommentsLoadingViewHolder(
                postActionsCallback::onReloadCommentsClicked,
                view
            )
            else -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SinglePostViewHolder -> holder.bind((item as CommentAdapterItem.PostItem).post)
            is CommentViewHolder -> holder.bind((item as CommentAdapterItem.CommentItem).comment)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && holder is SinglePostViewHolder) {
            try {
                val numberOfLikes = payloads[0] as Int
                holder.like(numberOfLikes)
                return
            } catch (e: Exception) {
                Log.e(TAG, "Payloads cast exception", e)
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is CommentAdapterItem.PostItem -> R.layout.post_with_image_list_item
        is CommentAdapterItem.CommentItem -> R.layout.comment_list_item
        is CommentAdapterItem.LoadingCommentsItem -> R.layout.loading_footer_item
        is CommentAdapterItem.FooterItem -> R.layout.comment_footer_item
        is CommentAdapterItem.ErrorLoadingItem -> R.layout.error_comments_loading_item
    }

    fun updatePost(likes: Int) {
        notifyItemChanged(0, likes)
    }

    fun setPost(post: Post) {
        val newList = mutableListOf<CommentAdapterItem>()
        newList.add(CommentAdapterItem.PostItem(post))
        submitList(newList)
    }

    fun showErrorMessage() {
        val newList = mutableListOf<CommentAdapterItem>()
        if (itemCount > 0) {
            newList.add(getItem(0))
        }
        newList.add(CommentAdapterItem.ErrorLoadingItem)
        submitList(newList)
    }

    fun setLoadingState() {
        val newList = mutableListOf<CommentAdapterItem>()
        newList.add(getItem(0))
        newList.add(CommentAdapterItem.LoadingCommentsItem)
        submitList(newList)
    }

    fun setComments(comments: List<Comment>) {
        val newList = mutableListOf<CommentAdapterItem>()
        newList.add( getItem(0))
        comments.forEach { comment ->
            newList.add(CommentAdapterItem.CommentItem(comment))
        }
        if (comments.isEmpty()) {
            newList.add(CommentAdapterItem.FooterItem)
        }
        submitList(newList)
    }
}

sealed class CommentAdapterItem {

    data class PostItem(val post: Post) : CommentAdapterItem()

    data class CommentItem(val comment: Comment) : CommentAdapterItem()

    object LoadingCommentsItem : CommentAdapterItem()

    object FooterItem : CommentAdapterItem()

    object ErrorLoadingItem : CommentAdapterItem()
}

private class CommentDiffCallback : DiffUtil.ItemCallback<CommentAdapterItem>() {
    override fun areItemsTheSame(
        oldItem: CommentAdapterItem,
        newItem: CommentAdapterItem
    ): Boolean {

        val isSamePostItem = oldItem is CommentAdapterItem.PostItem
                && newItem is CommentAdapterItem.PostItem
                && oldItem.post.id == newItem.post.id

        val isSameCommentItem = oldItem is CommentAdapterItem.CommentItem
                && newItem is CommentAdapterItem.CommentItem
                && oldItem.comment.id == newItem.comment.id

        val isSameFooterItem = (oldItem is CommentAdapterItem.FooterItem
                && newItem is CommentAdapterItem.FooterItem)

        val isSameLoadingItem = (oldItem is CommentAdapterItem.LoadingCommentsItem
                && newItem is CommentAdapterItem.LoadingCommentsItem)

        val isSameErrorItem = oldItem is CommentAdapterItem.ErrorLoadingItem
                && newItem is CommentAdapterItem.ErrorLoadingItem

        return isSamePostItem || isSameCommentItem || isSameFooterItem
                || isSameErrorItem || isSameLoadingItem
    }

    override fun areContentsTheSame(
        oldItem: CommentAdapterItem, newItem: CommentAdapterItem
    ): Boolean {
        return oldItem == newItem
    }
}