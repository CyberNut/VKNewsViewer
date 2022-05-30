package com.example.vknewsviewer.utils

inline fun getStartDay(date: Long): Long {
    return date - date % DAY_IN_MILLIS
}
