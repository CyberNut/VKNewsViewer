package com.example.vknewsviewer.ui.newslist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.BottomNavBarVisibleHelper
import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.ui.main.MainActivityViewModel
import com.example.vknewsviewer.ui.newslist.adapter.ItemTouchHelperCallback
import com.example.vknewsviewer.ui.newslist.adapter.PostListAdapter
import com.example.vknewsviewer.ui.newslist.adapter.TimeDivider
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

abstract class PostListFragment : Fragment(), PostListView, NewsActionsCallback {

    private lateinit var bottomNavBarVisibleHelper: BottomNavBarVisibleHelper
    private lateinit var adapter: PostListAdapter

    @Inject
    lateinit var viewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainActivityComponent.inject(this)
        try {
            bottomNavBarVisibleHelper = context as BottomNavBarVisibleHelper
        } catch (ex: ClassCastException) {
            throw ClassCastException("$context must implement BottomNavBarVisibleHelper")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postListPresenter.attachView(this)
        bottomNavBarVisibleHelper.setBottomNavBarVisibility(true)
        adapter = getAdapter(this)
        prepareUI()
        loadPosts()
    }

    override fun onDestroyView() {
        viewModel.postListPresenter.detachView(requireActivity().isFinishing)
        newsRecyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onPostClicked(ownerId: Int, postId: Int) {
        (requireActivity() as MainActivity).showNewsPostFragment(ownerId, postId)
    }

    override fun onPostLiked(ownerId: Int, postId: Int) {
        viewModel.postListPresenter.likePost(ownerId, postId)
    }

    override fun onPostDeleted(id: Int) {
        viewModel.postListPresenter.deletePost(id)
    }

    override fun onPostLogoClicked(userGroupId: Int) {
        viewModel.postListPresenter.showUserProfile(userGroupId)
    }

    override fun showPosts(posts: List<Post>) {
        adapter.submitList(posts)
        if (posts.isNotEmpty()) {
            shimmerViewContainer.isVisible = false
            showEmptyPlaceHolder(false)
            newsRecyclerView.isVisible = true
        }
    }

    override fun updateBottomNavBar(isLikedPostListEmpty: Boolean) {
        bottomNavBarVisibleHelper.setFavoritesTabVisibility(isLikedPostListEmpty)
    }

    override fun showConnectionError() {
        showConnectionErrorDialog()
    }

    override fun showEmptyState() {
        showEmptyPlaceHolder(true)
        newsRecyclerView.isVisible = false
        shimmerViewContainer.isVisible = false
        loadingProgressBar.isVisible = false
    }

    override fun showLoadingState(isLoading: Boolean) {
        loadingProgressBar.isVisible = isLoading
    }

    override fun updatePostLikes(id: Int, likes: Int) {
        updateLikedPost(id, likes)
    }

    override fun showUserProfile(userId: Int) {
        (requireActivity() as MainActivity).showUserProfileFragment(userId)
    }

    override fun showUnsupportedOperationMessage() {
        Toast.makeText(requireContext(), getString(R.string.demo_version), Toast.LENGTH_SHORT)
            .show()
    }

    private fun prepareUI() {
        newsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(requireContext(), adapter))
        itemTouchHelper.attachToRecyclerView(newsRecyclerView)

        val timeDivider: RecyclerView.ItemDecoration = TimeDivider(requireContext())
        newsRecyclerView.addItemDecoration(timeDivider)

        swipeRefreshLayout.setOnRefreshListener {
            loadPosts(true)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateLikedPost(id: Int, likes: Int) {
        adapter.updateLikedPost(id, likes)
    }

    private fun showConnectionErrorDialog() {
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.connection_error))
            .setPositiveButton(
                getString(R.string.ok)
            ) { _, _ -> }
            .create()
        alertDialog.show()
        shimmerViewContainer.isVisible = false
    }

    private fun showEmptyPlaceHolder(emptyPlaceHolderVisible: Boolean) {
        noDataTextView.isVisible = emptyPlaceHolderVisible
        newsRecyclerView.isVisible = !emptyPlaceHolderVisible
    }

    abstract fun getAdapter(newsActionsCallback: NewsActionsCallback): PostListAdapter

    abstract fun loadPosts(forceUpdate: Boolean = false)
}