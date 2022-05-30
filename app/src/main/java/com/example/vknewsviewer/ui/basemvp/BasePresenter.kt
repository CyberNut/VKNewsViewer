package com.example.vknewsviewer.ui.basemvp

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T: MvpView>: Presenter<T> {

    protected val compositeDisposable = CompositeDisposable()
    private var attachedView: T? = null
    val view: T?
        get() = attachedView

    override fun attachView(view: T) {
        attachedView = view
    }

    override fun detachView(isFinishing: Boolean) {
        if(isFinishing) {
            compositeDisposable.clear()
        }
        attachedView = null
    }
}