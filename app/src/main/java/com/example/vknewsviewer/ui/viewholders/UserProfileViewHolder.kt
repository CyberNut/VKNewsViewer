package com.example.vknewsviewer.ui.viewholders

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.VkUserProfile
import com.example.vknewsviewer.utils.EMPTY_STRING
import kotlinx.android.synthetic.main.user_profile_list_item.view.*

class UserProfileViewHolder(
    private val itsOwnerProfile: Boolean,
    sendWallPostFunc: (Int, String) -> Unit,
    root: View
) : RecyclerView.ViewHolder(root) {

    private var userId: Int = 0

    init {
        itemView.sendNewWallPostButton.setOnClickListener {
            if (itemView.newWallPostEditText.text.toString().isNotEmpty()) {
                sendWallPostFunc(userId, itemView.newWallPostEditText.text.toString())
            }
        }
    }

    fun bind(userProfile: VkUserProfile) {
        userId = userProfile.id
        itemView.userNickNameTextView.text = userProfile.userName + " (" + userProfile.domain + ")"
        with(itemView.context) {
            itemView.cityTextView.text = getString(R.string.city, userProfile.city)
            itemView.cityTextView.isVisible = userProfile.city?.isNotEmpty() ?: false
            itemView.educationTextView.text = getString(R.string.education, userProfile.education)
            itemView.educationTextView.isVisible = userProfile.education?.isNotEmpty() ?: false
            itemView.followersCountTextView.text =
                getString(R.string.followers_count, userProfile.followersCount)
            itemView.lastSeenTextView.text = if (userProfile.online) {
                getString(R.string.online)
            } else {
                getString(R.string.was_online, userProfile.getLastSeenDateTimeString())
            }
            itemView.careerTextView.text = getString(R.string.career, userProfile.currentJobInfo)
            itemView.careerTextView.isVisible = userProfile.currentJobInfo?.isNotEmpty() ?: false
            itemView.birthDateTextView.text = userProfile.birthDate
            itemView.birthDateTextView.isVisible = userProfile.birthDate?.isNotEmpty() ?: false
            itemView.aboutTextView.text = userProfile.aboutInfo
            itemView.aboutTextView.isVisible = userProfile.aboutInfo?.isNotEmpty() ?: false
            if (itsOwnerProfile) {
                itemView.newWallPostEditText.hint = getString(R.string.what_is_new)
            } else {
                itemView.newWallPostEditText.hint = getString(R.string.write_somethings)
            }
        }
        Glide.with(itemView.context).load(userProfile.photoImageUrl)
            .into(itemView.userPhotoImageView)
    }

    fun cleanNewWallPostEditText() {
        itemView.newWallPostEditText.setText(EMPTY_STRING)
    }
}
