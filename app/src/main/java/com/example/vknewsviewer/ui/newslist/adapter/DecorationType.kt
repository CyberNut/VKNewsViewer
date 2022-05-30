package com.example.vknewsviewer.ui.newslist.adapter

sealed class DecorationType {

    data class DividerWithText(val dividerDate: Long) : DecorationType()

    object SimpleDivider : DecorationType()

}
