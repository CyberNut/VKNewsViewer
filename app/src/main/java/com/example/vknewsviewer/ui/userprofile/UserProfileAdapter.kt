package com.example.vknewsviewer.ui.userprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUserProfile
import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.viewholders.ErrorLoadingViewHolder
import com.example.vknewsviewer.ui.viewholders.LoadingFooterViewHolder
import com.example.vknewsviewer.ui.viewholders.PostFooterViewHolder
import com.example.vknewsviewer.ui.viewholders.PostWithImageViewHolder
import com.example.vknewsviewer.ui.viewholders.UserProfileViewHolder

private const val IS_NEED_UPDATE_COMMENT_EDIT_TEXT = true

class UserProfileAdapter(
    private val itsOwnerProfile: Boolean,
    private val newsActionsCallback: NewsActionsCallback,
    private val sendWallPostFunc: (Int, String) -> Unit
) :
    ListAdapter<ProfileAdapterItem, RecyclerView.ViewHolder>(ProfileItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.post_with_image_list_item -> PostWithImageViewHolder(newsActionsCallback, view)
            R.layout.user_profile_list_item -> UserProfileViewHolder(
                itsOwnerProfile,
                sendWallPostFunc,
                view
            )
            R.layout.error_loading_item -> ErrorLoadingViewHolder(view)
            R.layout.comment_footer_item -> PostFooterViewHolder(view)
            R.layout.loading_footer_item -> LoadingFooterViewHolder(view)
            else -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is UserProfileViewHolder -> holder.bind((item as ProfileAdapterItem.UserProfileItem).profile)
            is PostWithImageViewHolder -> holder.bind((item as ProfileAdapterItem.PostItem).post)
            is ErrorLoadingViewHolder -> holder.bind((item as ProfileAdapterItem.ErrorLoadingItem).message)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && holder is UserProfileViewHolder) {
            holder.cleanNewWallPostEditText()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ProfileAdapterItem.PostItem -> R.layout.post_with_image_list_item
        is ProfileAdapterItem.UserProfileItem -> R.layout.user_profile_list_item
        is ProfileAdapterItem.ErrorLoadingItem -> R.layout.error_loading_item
        is ProfileAdapterItem.FooterItem -> R.layout.comment_footer_item
        is ProfileAdapterItem.LoadingFooterItem -> R.layout.loading_footer_item
    }

    fun setUserProfile(profile: VkUserProfile) {
        val newList = mutableListOf<ProfileAdapterItem>()
        newList.add(ProfileAdapterItem.UserProfileItem(profile))
        submitList(newList)
    }

    fun setErrorMessage(message: String) {
        val profileItem = getItem(0)
        val newList = mutableListOf<ProfileAdapterItem>()
        newList.add(0, profileItem)
        newList.add(ProfileAdapterItem.ErrorLoadingItem(message))
        submitList(newList)
    }

    fun setPosts(posts: List<Post>) {
        val profileItem = getItem(0)
        val newList = mutableListOf<ProfileAdapterItem>()
        newList.add(0, profileItem)
        if (posts.isNotEmpty()) {
            posts.forEach { post ->
                newList.add(ProfileAdapterItem.PostItem(post))
            }
        } else {
            newList.add(ProfileAdapterItem.FooterItem)
        }
        submitList(newList)
    }

    fun setLoadingState(isLoading: Boolean) {
        val newList = mutableListOf<ProfileAdapterItem>()
        if (isLoading) {
            if (itemCount > 0) {
                val profileItem = getItem(0)
                newList.add(0, profileItem)
            }
            newList.add(ProfileAdapterItem.LoadingFooterItem)
        } else {
            newList.addAll(currentList.filter { item -> item !is ProfileAdapterItem.LoadingFooterItem })
        }
        submitList(newList)
    }

    fun onSuccessSendingComment() {
        notifyItemChanged(0, IS_NEED_UPDATE_COMMENT_EDIT_TEXT)
    }
}

sealed class ProfileAdapterItem {

    class UserProfileItem(val profile: VkUserProfile) : ProfileAdapterItem()

    class PostItem(val post: Post) : ProfileAdapterItem()

    class ErrorLoadingItem(val message: String): ProfileAdapterItem()

    object FooterItem : ProfileAdapterItem()

    object LoadingFooterItem : ProfileAdapterItem()
}

private class ProfileItemDiffCallback : DiffUtil.ItemCallback<ProfileAdapterItem>() {

    override fun areItemsTheSame(
        oldItem: ProfileAdapterItem,
        newItem: ProfileAdapterItem
    ): Boolean {

        val isSameProfileItem = oldItem is ProfileAdapterItem.UserProfileItem
                && newItem is ProfileAdapterItem.UserProfileItem
                && oldItem.profile.id == newItem.profile.id

        val isSamePostItem = oldItem is ProfileAdapterItem.PostItem
                && newItem is ProfileAdapterItem.PostItem
                && oldItem.post.id == newItem.post.id

        val isSameFooterItem = (oldItem is ProfileAdapterItem.FooterItem
                && newItem is ProfileAdapterItem.FooterItem)

        val isSameLoadingItem = (oldItem is ProfileAdapterItem.LoadingFooterItem
                && newItem is ProfileAdapterItem.LoadingFooterItem)

        val isSameErrorLoadingItem = (oldItem is ProfileAdapterItem.ErrorLoadingItem
                && newItem is ProfileAdapterItem.ErrorLoadingItem)

        return isSamePostItem || isSameProfileItem || isSameFooterItem || isSameLoadingItem || isSameErrorLoadingItem
    }

    override fun areContentsTheSame(
        oldItem: ProfileAdapterItem, newItem: ProfileAdapterItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ProfileAdapterItem, newItem: ProfileAdapterItem): Any? {
        return if (oldItem is ProfileAdapterItem.UserProfileItem
            && newItem is ProfileAdapterItem.UserProfileItem) {
            IS_NEED_UPDATE_COMMENT_EDIT_TEXT
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}