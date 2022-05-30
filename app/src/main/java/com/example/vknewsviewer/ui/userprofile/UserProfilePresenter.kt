package com.example.vknewsviewer.ui.userprofile

import android.util.Log
import com.example.vknewsviewer.di.ActivityScope
import com.example.vknewsviewer.repository.Repository
import com.example.vknewsviewer.ui.basemvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class UserProfilePresenter @Inject constructor(private val repository: Repository) :
    BasePresenter<UserProfileView>() {

    private val TAG = "UserProfilePresenter"
    private var usingCache: Boolean = false
    private var isLoading: Boolean = false
    private var isLikeSending: Boolean = false
    private var itsFirstLoading: Boolean = true

    fun getUserProfile(userId: Int, itsOwnerProfile: Boolean, forceUpdate: Boolean = false) {
        if (isLoading) return
        isLoading = true
        usingCache = itsOwnerProfile
        view?.showProfileLoadingProgress(true)
        compositeDisposable.add(
            repository.getUserProfile(userId, usingCache, forceUpdate || itsFirstLoading)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .doFinally {
                    isLoading = false
                    itsFirstLoading = false
                    view?.showProfileLoadingProgress(false)
                }
                .subscribeBy(onError = { error ->
                    if (!itsOwnerProfile) {
                        view?.showOpenProfileError()
                    }
                    Log.e(TAG, "getting user profile error in PostPresenter", error)
                }, onNext = { userProfile ->
                    view?.showUserProfile(userProfile)
                    getWallPosts(userId, usingCache)
                })
        )
    }

    fun likePost(ownerId: Int, postId: Int) {
        if (isLikeSending) {
            return
        }
        isLikeSending = true
        compositeDisposable.add(
            repository.likePost(ownerId, postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { isLikeSending = false }
                .subscribeBy(onSuccess = { idAndLikes ->
                    view?.updatePostLikes(idAndLikes.first, idAndLikes.second)
                }, onError = { error ->
                    view?.showErrorMessage(error.localizedMessage, true)
                    Log.e(TAG, "Like post error in presenter", error)
                }
                )
        )
    }

    fun sendNewWallPost(userId: Int, message: String) {
        compositeDisposable.add((
                repository.sendWallPost(userId, message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onError = { error ->
                        Log.e(TAG, "getting wall posts error in UserProfilePresenter", error)
                        view?.apply {
                            showErrorMessage(error.localizedMessage, true)
                            onSendingCommentError()
                        }
                    }, onComplete = {
                        getWallPosts(userId, usingCache)
                        view?.onSendingCommentSuccess()
                    }
                    )
                ))
    }

    fun showUserProfile(userId: Int) {
        if (userId > 0) {
            view?.openUserProfile(userId)
        } else {
            view?.showUnsupportedOperationMessage()
        }
    }

    private fun getWallPosts(userId: Int, usingCache: Boolean) {
        compositeDisposable.add(
            repository.getWallPosts(userId, usingCache)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    view?.showPostLoadingProgress(true)
                }
                .doFinally {
                    view?.showPostLoadingProgress(false)
                }
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeBy(onError = { error ->
                    view?.showErrorMessage(error.localizedMessage, false)
                    Log.e(TAG, "getting wall posts error in UserProfilePresenter", error)
                }, onNext = { posts ->
                    view?.showUserWall(posts)
                })
        )
    }
}