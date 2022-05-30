package com.example.vknewsviewer.ui.newslist

import android.util.Log
import com.example.vknewsviewer.di.ActivityScope
import com.example.vknewsviewer.repository.Repository
import com.example.vknewsviewer.ui.basemvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class PostListPresenter @Inject constructor(private val repository: Repository) :
    BasePresenter<PostListView>() {

    private val TAG = "PostListPresenter"
    private var isLoading: Boolean = false
    private var itsFirstLoading: Boolean = true

    fun loadPosts(forceUpdate: Boolean = false) {
        if (isLoading) return
        isLoading = true
        val isNeedToLoadFromNetwork = forceUpdate || itsFirstLoading
        compositeDisposable.add(repository.getPosts(isNeedToLoadFromNetwork)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .doOnSubscribe {
                if (isNeedToLoadFromNetwork) {
                    view?.showLoadingState(true)
                }
            }
            .doFinally {
                if (isNeedToLoadFromNetwork) {
                    view?.showLoadingState(false)
                }
                isLoading = false
                itsFirstLoading = false
            }
            .subscribeBy(onNext = { posts ->
                val isLikedPostListEmpty = !posts.any { post -> post.isLiked }
                view?.apply {
                    showPosts(posts)
                    updateBottomNavBar(isLikedPostListEmpty)
                }
            }, onError = { error ->
                Log.e(TAG, "Loading post error in presenter", error)
                view?.showConnectionError()
            }
            )
        )
    }

    fun loadFavoritesPosts() {
        if (isLoading) return
        isLoading = true
        compositeDisposable.add(repository.getLikedPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoadingState(true) }
            .doFinally {
                view?.showLoadingState(false)
                isLoading = false
            }
            .subscribeBy(onSuccess = { posts ->
                view?.showPosts(posts)
            }, onError = { error ->
                Log.e(TAG, "Loading post error in presenter", error)
                view?.showConnectionError()
            }
            )
        )
    }

    fun deletePost(id: Int) {
        compositeDisposable.add(
            repository.deletePost(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onComplete = {
                    getLikedPostsForUpdateNavBar()
                }, onError = { error ->
                    Log.e(TAG, "Delete post error in presenter", error)
                    view?.showConnectionError()
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
                    view?.updatePostLikes(idAndLikes.first, idAndLikes.second)
                    getLikedPostsForUpdateNavBar()
                }, onError = { error ->
                    Log.e(TAG, "Like post error in presenter", error)
                    view?.showConnectionError()
                }
                )
        )
    }

    fun showUserProfile(userId: Int) {
        if (userId > 0) {
            view?.showUserProfile(userId)
        } else {
            view?.showUnsupportedOperationMessage()
        }
    }

    private fun getLikedPostsForUpdateNavBar() {
        compositeDisposable.add(
            repository.getLikedPosts()
                .subscribeOn(Schedulers.io())
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