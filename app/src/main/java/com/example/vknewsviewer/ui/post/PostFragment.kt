package com.example.vknewsviewer.ui.post

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Comment
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.ui.BottomNavBarVisibleHelper
import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.utils.showMessage
import kotlinx.android.synthetic.main.fragment_news_post.*
import javax.inject.Inject

class PostFragment : Fragment(), PostView, PostActionsCallback {

    private val TAG = "NewsPostFragment"
    private var postId: Int = 0
    private var ownerId: Int = 0
    private lateinit var bottomNavBarVisibleHelper: BottomNavBarVisibleHelper
    private lateinit var postAndCommentsAdapter: PostAndCommentsAdapter

    @Inject
    lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            postId = args.getInt(ARG_POST_ID)
            ownerId = args.getInt(ARG_OWNER_ID)
        }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavBarVisibleHelper.setBottomNavBarVisibility(false)
        viewModel.presenter.attachView(this)
        return inflater.inflate(R.layout.fragment_news_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.presenter.getPost(ownerId, postId)
        prepareUI()
    }

    override fun onDestroyView() {
        viewModel.presenter.detachView(requireActivity().isFinishing)
        super.onDestroyView()
    }

    override fun showProgressBar() {
        postAndCommentsAdapter.setLoadingState()
    }

    override fun showPost(post: Post) {
        postAndCommentsAdapter.setPost(post)
    }

    override fun updateComments(comments: List<Comment>) {
        postAndCommentsAdapter.setComments(comments)
    }

    override fun showCommentsLoadingError() {
        postAndCommentsAdapter.showErrorMessage()
    }

    override fun onSendingCommentError(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSendingCommentSuccess() {
        newCommentEditText.setText("")
        (requireActivity() as MainActivity).hideKeyboard()
    }

    override fun updatePostLikes(likes: Int) {
        postAndCommentsAdapter.updatePost(likes)
    }

    override fun onLikePostClicked(post: Post) {
        viewModel.presenter.likePost(post.ownerId, post.id)
    }

    override fun onReloadCommentsClicked() {
        viewModel.presenter.getComments()
    }

    override fun onSharePostClicked(post: Post) {
        saveImageIntoGallery(post.imageUrl, true)
    }

    override fun onImagePostClicked(post: Post) {
        saveImageIntoGallery(post.imageUrl, false)
    }

    override fun updateBottomNavBar(isLikedPostListEmpty: Boolean) {
        bottomNavBarVisibleHelper.setFavoritesTabVisibility(isLikedPostListEmpty)
    }

    override fun showLikePostError() {
        Toast.makeText(requireContext(), R.string.unexpected_error, Toast.LENGTH_SHORT).show()
    }

    private fun prepareUI() {
        postAndCommentsAdapter = PostAndCommentsAdapter(this, ::onUserLogoClicked)
        postRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        postRecyclerView.adapter = postAndCommentsAdapter
        postRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        sendCommentaryButton.setOnClickListener {
            viewModel.presenter.sendComment(newCommentEditText.text.toString())
        }
    }

    private fun onUserLogoClicked(id: Int) {
        (requireActivity() as MainActivity).showUserProfileFragment(id)
    }

    private fun saveImageIntoGallery(imageUrl: String, isNeedShareImage: Boolean) {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return
        val fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
        Glide.with(requireContext())
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveBitmapToFile(resource, fileName, isNeedShareImage)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun saveBitmapToFile(image: Bitmap, fileName: String, isNeedShareImage: Boolean) {
        val mediaStoreVolume: String
        val isSupportPendingStatus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        if (!isSupportPendingStatus) {
            if (!checkWriteStoragePermission()) return
            mediaStoreVolume = MediaStore.VOLUME_EXTERNAL
        } else {
            mediaStoreVolume = MediaStore.VOLUME_EXTERNAL_PRIMARY
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        try {
            val resolver = requireContext().contentResolver
            val collection = MediaStore.Images.Media.getContentUri(mediaStoreVolume)
            val imageUri = resolver.insert(collection, contentValues)
            if (imageUri != null) {
                resolver.openOutputStream(imageUri).use { out ->
                    image.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                if (isSupportPendingStatus) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                resolver.update(imageUri, contentValues, null, null)
                requireContext().showMessage(getString(R.string.image_saved_to_gallery))
                if (isNeedShareImage) shareImage(imageUri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "saving bitmap to file error", e)
        }
    }

    private fun checkWriteStoragePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
            false
        }
    }

    private fun shareImage(imageUri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = INTENT_TYPE_IMAGE
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.sharing_image)))
    }

    companion object {

        private const val ARG_POST_ID = "ARG_POST_ID"
        private const val ARG_OWNER_ID = "ARG_OWNER_ID"
        private const val REQUEST_CODE = 0
        private const val INTENT_TYPE_IMAGE = "image/jpeg"

        @JvmStatic
        fun newInstance(ownerId: Int, postId: Int) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_OWNER_ID, ownerId)
                    putInt(ARG_POST_ID, postId)
                }
            }
    }
}