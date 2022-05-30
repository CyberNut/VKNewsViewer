package com.example.vknewsviewer.ui.post

import androidx.lifecycle.ViewModel
import com.example.vknewsviewer.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class PostViewModel @Inject constructor(var presenter: PostPresenter): ViewModel()
