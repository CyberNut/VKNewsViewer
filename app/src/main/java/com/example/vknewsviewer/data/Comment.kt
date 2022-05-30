package com.example.vknewsviewer.data

import com.example.vknewsviewer.utils.EMPTY_STRING
import com.example.vknewsviewer.utils.POST_HEADER_DATE_FORMAT
import java.text.SimpleDateFormat

data class Comment(
    val id: Int,
    val text: String = EMPTY_STRING,
    val user: VkUser? = null,
    val date: Long = 0
) {
    fun getDateTimeString(): String {
        val sdf = SimpleDateFormat(POST_HEADER_DATE_FORMAT)
        return sdf.format(date)
    }
}
