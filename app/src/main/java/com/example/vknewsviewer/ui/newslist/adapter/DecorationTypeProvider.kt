package com.example.vknewsviewer.ui.newslist.adapter

interface DecorationTypeProvider {

    fun getType(position: Int): DecorationType
}