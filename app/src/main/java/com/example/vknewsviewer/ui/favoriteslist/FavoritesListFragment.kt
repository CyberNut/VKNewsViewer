package com.example.vknewsviewer.ui.favoriteslist

import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.newslist.PostListFragment

class FavoritesListFragment : PostListFragment() {

    override fun getAdapter(newsActionsCallback: NewsActionsCallback) =
        FavoritesPostListAdapter(newsActionsCallback)

    override fun loadPosts(forceUpdate: Boolean) {
        viewModel.postListPresenter.loadFavoritesPosts()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoritesListFragment()
    }
}