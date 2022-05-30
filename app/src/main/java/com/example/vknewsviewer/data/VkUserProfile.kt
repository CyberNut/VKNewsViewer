package com.example.vknewsviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vknewsviewer.utils.POST_HEADER_DATE_FORMAT
import java.text.SimpleDateFormat

@Entity(tableName = "user_profile")
data class VkUserProfile(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "photo_image_url")
    val photoImageUrl: String,
    @ColumnInfo(name = "birth_date")
    val birthDate: String?,
    val city: String?,
    val education: String?,
    val online: Boolean,
    val domain: String,
    @ColumnInfo(name = "last_seen_date")
    val lastSeenDate: Long,
    @ColumnInfo(name = "current_job_info")
    val currentJobInfo: String?,
    @ColumnInfo(name = "about_info")
    val aboutInfo: String?,
    @ColumnInfo(name = "followers_count")
    val followersCount: Int
) {
    fun getLastSeenDateTimeString(): String {
        val sdf = SimpleDateFormat(POST_HEADER_DATE_FORMAT)
        return sdf.format(lastSeenDate)
    }
}



