package com.example.vknewsviewer.ui.post

import android.content.Context
import android.util.Log
import com.example.vknewsviewer.R
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.di.ActivityScope
import com.example.vknewsviewer.repository.Repository
import com.example.vknewsviewer.ui.basemvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class PostPresenter @Inject constructor(private val repository: Repository, private val context: Context) :
    BasePresenter<PostView>() {

    private val TAG = "PostPresenter"
    private lateinit var post: Post

    fun getPost(ownerId: Int, postId: Int) {
        compositeDisposable.add(
            repository.getPost(ownerId, postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onSuccess = { postFromDb ->
                    post = postFromDb
                    view?.showPost(post)
                    getComments()
                }, onError = { error ->
                    Log.e(TAG, "get post error in PostPresenter", error)
                }
                )
        )
    }

    fun likePost(ownerId: Int, postId: Int) {
        compositeDisposable.add(
            repository.likePost(ownerId, postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onSuccess = { idAndLikes ->
                    view?.updatePostLikes(idAndLikes.second)
                    getLikedPostsForUpdateNavBar()
                }, onError = { error ->
                    view?.showLikePostError()
                    Log.e(TAG, "Like post error in presenter", error)
                }
                )
        )
    }

    fun getComments() {
        compositeDisposable.add(
            repository.getComments(post).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    view?.showProgressBar()
                }
                .subscribeBy(onError = { error ->
                    Log.e(TAG, "Getting comments error in presenter", error)
                    view?.showCommentsLoadingError()
                }, onSuccess = { comments ->
                    view?.updateComments(comments)
                })
        )
    }

    fun sendComment(commentText: String) {
        if (post.canComment) {
            compositeDisposable.add(
                repository.sendComment(post, commentText).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onError = { error ->
                        Log.e(TAG, "Send comment error in presenter", error)
                        view?.onSendingCommentError(context.getString(R.string.failed_to_send_comment))
                    }, onComplete = {
                        getComments()
                        view?.onSendingCommentSuccess()
                    })
            )
        } else {
            view?.onSendingCommentError(context.getString(R.string.disabled_comment))
        }
    }

    private fun getLikedPostsForUpdateNavBar() {
        compositeDisposable.add(repository.getLikedPosts().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { likedPosts ->
                view?.updateBottomNavBar(likedPosts.isEmpty())
            }, onError = { error ->
                Log.e(TAG, "Getting liked posts error in presenter", error)
            }
            )
        )
    }
}