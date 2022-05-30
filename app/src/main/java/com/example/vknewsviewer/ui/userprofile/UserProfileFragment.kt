package com.example.vknewsviewer.ui.userprofile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUserProfile
import com.example.vknewsviewer.ui.BottomNavBarVisibleHelper
import com.example.vknewsviewer.ui.NewsActionsCallback
import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.utils.LoginSettingsStorage
import kotlinx.android.synthetic.main.fragment_user_profile.*
import javax.inject.Inject


private const val ARG_USER_ID = "userId"

class UserProfileFragment : Fragment(), NewsActionsCallback, UserProfileView {

    private var userId: Int = 0
    private lateinit var bottomNavBarVisibleHelper: BottomNavBarVisibleHelper
    private lateinit var adapter: UserProfileAdapter
    private var itsOwnerProfile: Boolean = false

    @Inject
    lateinit var loginSettingsStorage: LoginSettingsStorage

    @Inject
    lateinit var viewModel: UserProfileViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainActivityComponent.inject(this)
        try {
            bottomNavBarVisibleHelper = context as BottomNavBarVisibleHelper
        } catch (ex: ClassCastException) {
            throw ClassCastException("$context must implement BottomNavBarVisibleHelper")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.presenter.attachView(this)
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itsOwnerProfile = loginSettingsStorage.getUserId() == userId
        bottomNavBarVisibleHelper.setBottomNavBarVisibility(itsOwnerProfile)
        prepareUI()
        viewModel.presenter.getUserProfile(userId, itsOwnerProfile)
    }

    override fun onDestroyView() {
        viewModel.presenter.detachView(true)
        super.onDestroyView()
    }

    override fun showUserProfile(userProfile: VkUserProfile) {
        adapter.setUserProfile(userProfile)
    }

    override fun showUserWall(posts: List<Post>) {
        adapter.setPosts(posts)
    }

    override fun onPostClicked(ownerId: Int, postId: Int) {
        (activity as MainActivity).showNewsPostFragment(ownerId, postId)
    }

    override fun onPostLiked(ownerId: Int, postId: Int) {
        viewModel.presenter.likePost(ownerId, postId)
    }

    override fun openUserProfile(userId: Int) {
        (requireActivity() as MainActivity).showUserProfileFragment(userId)
    }

    override fun onPostLogoClicked(id: Int) {
        viewModel.presenter.showUserProfile(id)
    }

    override fun showErrorMessage(message: String, onlyToast: Boolean) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        if (!onlyToast) {
            adapter.setErrorMessage(message)
        }
    }

    override fun showPostLoadingProgress(isLoading: Boolean) {
        adapter.setLoadingState(isLoading)
    }

    override fun showProfileLoadingProgress(isLoading: Boolean) {
        profileLoadingProgressBar.isVisible = isLoading
    }

    override fun showOpenProfileError() {
        Toast.makeText(
            requireContext(),
            getString(R.string.open_user_profile_error_message),
            Toast.LENGTH_SHORT
        ).show()
        (requireActivity() as MainActivity).showUserProfileFragment(loginSettingsStorage.getUserId())
    }

    override fun updatePostLikes(postId: Int, likesCount: Int) {
        updateLikedPost(postId, likesCount)
    }

    override fun onPostDeleted(id: Int) {
        Toast.makeText(
            requireContext(),
            getString(R.string.demo_version),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSendingCommentError() {
        Toast.makeText(
            requireContext(),
            getString(R.string.failed_to_send_comment),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSendingCommentSuccess() {
        adapter.onSuccessSendingComment()
        (requireActivity() as MainActivity).hideKeyboard()
    }

    override fun showUnsupportedOperationMessage() {
        Toast.makeText(requireContext(), getString(R.string.demo_version), Toast.LENGTH_SHORT)
            .show()
    }

    private fun sendNewWallPost(userId: Int, message: String) {
        viewModel.presenter.sendNewWallPost(userId, message)
    }

    private fun updateLikedPost(id: Int, likes: Int) {
        for (i in 0 until profileRecyclerView.childCount) {
            val child = profileRecyclerView.getChildAt(i)
            val adapterPosition = profileRecyclerView.getChildAdapterPosition(child)
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return
            }
            val profileAdapterItem = adapter.currentList[adapterPosition]
            profileAdapterItem?.let {
                if (profileAdapterItem is ProfileAdapterItem.PostItem && profileAdapterItem.post.id == id) {
                    profileAdapterItem.post.like(likes)
                    adapter.notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    private fun prepareUI() {
        adapter = UserProfileAdapter(itsOwnerProfile, this, ::sendNewWallPost)
        profileRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        profileRecyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        dividerItemDecoration.setDrawable(requireContext().getDrawable(R.drawable.post_divider)!!)
        profileRecyclerView.addItemDecoration(dividerItemDecoration)
        profileSwipeRefreshLayout.setOnRefreshListener {
            profileSwipeRefreshLayout.isRefreshing = false
            viewModel.presenter.getUserProfile(userId, itsOwnerProfile, true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}
