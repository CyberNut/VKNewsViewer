package com.example.vknewsviewer.ui.login

import com.example.vknewsviewer.ui.basemvp.MvpView

interface LoginMVPView: MvpView {

    fun startLoginFlow()

    fun setAutoLoginFlag(autoLoginFlag: Boolean)

    fun startMainActivity()

}