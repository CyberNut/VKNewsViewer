package com.example.vknewsviewer.ui.basemvp

interface Presenter<T: MvpView> {

    fun attachView(view: T)

    fun detachView(isFinishing: Boolean)

    fun viewIsReady() {}
}