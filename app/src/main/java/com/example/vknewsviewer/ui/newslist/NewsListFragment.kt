package com.example.vknewsviewer.ui.newslist

import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.newslist.adapter.PostListAdapter

class NewsListFragment : PostListFragment() {

    override fun getAdapter(newsActionsCallback: NewsActionsCallback) =
        PostListAdapter(newsActionsCallback)

    override fun loadPosts(forceUpdate: Boolean) {
        viewModel.postListPresenter.loadPosts(forceUpdate)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewsListFragment()
    }
}