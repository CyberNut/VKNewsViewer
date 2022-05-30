package com.example.vknewsviewer.di

import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.ui.favoriteslist.FavoritesListFragment
import com.example.vknewsviewer.ui.newslist.NewsListFragment
import com.example.vknewsviewer.ui.newslist.PostListFragment
import com.example.vknewsviewer.ui.post.PostFragment
import com.example.vknewsviewer.ui.userprofile.UserProfileFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(fragment: PostListFragment)
    fun inject(fragment: NewsListFragment)
    fun inject(fragment: FavoritesListFragment)
    fun inject(fragment: PostFragment)
    fun inject(fragment: UserProfileFragment)
}