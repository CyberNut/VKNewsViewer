package com.example.vknewsviewer.ui.main

import androidx.lifecycle.ViewModel
import com.example.vknewsviewer.di.ActivityScope
import com.example.vknewsviewer.ui.newslist.PostListPresenter
import javax.inject.Inject

@ActivityScope
class MainActivityViewModel @Inject constructor(val postListPresenter: PostListPresenter): ViewModel()
