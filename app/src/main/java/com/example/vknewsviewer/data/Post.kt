package com.example.vknewsviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vknewsviewer.utils.EMPTY_STRING
import com.example.vknewsviewer.utils.POST_HEADER_DATE_FORMAT
import java.text.SimpleDateFormat

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "source_id")
    val sourceId : Int = 0,
    @ColumnInfo(name = "owner_id")
    val ownerId : Int = 0,
    @ColumnInfo(name = "date")
    val date: Long = 0,
    @ColumnInfo(name = "number_of_likes")
    var numberOfLikes: Int = 0,
    @ColumnInfo(name = "is_liked")
    var isLiked: Boolean = false,
    @ColumnInfo(name = "group")
    var group: String = EMPTY_STRING,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int = 0,
    @ColumnInfo(name = "can_comment")
    val canComment: Boolean = true,
    @ColumnInfo(name = "text")
    val text: String = EMPTY_STRING,
    @ColumnInfo(name = "logo_image_url")
    var logoImageUrl: String = EMPTY_STRING,
    @ColumnInfo(name = "image_url")
    val imageUrl: String = EMPTY_STRING,
    @ColumnInfo(name = "itsWallPost")
    val wallPostFlag: Boolean = false
) {
    fun like(likes: Int) {
        if (!isLiked) {
            isLiked = true
            numberOfLikes = likes
        } else {
            isLiked = false
            numberOfLikes = likes
        }
    }

    fun getDateTimeString(): String {
        val sdf = SimpleDateFormat(POST_HEADER_DATE_FORMAT)
        return sdf.format(date)
    }
}

