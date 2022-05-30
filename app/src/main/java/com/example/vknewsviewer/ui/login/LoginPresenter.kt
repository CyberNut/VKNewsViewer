package com.example.vknewsviewer.ui.login

import android.content.Context
import com.example.vknewsviewer.NewsApplication
import com.example.vknewsviewer.utils.LoginSettingsStorage
import com.example.vknewsviewer.ui.basemvp.BasePresenter
import com.vk.api.sdk.auth.VKAccessToken
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val loginSettingsStorage: LoginSettingsStorage,
    private val appContext: Context
) :
    BasePresenter<LoginMVPView>() {

    override fun attachView(view: LoginMVPView) {
        super.attachView(view)
        loadSettings()
    }

    fun doLogin() {
        view?.startLoginFlow()
    }

    private fun loadSettings() {
        val autoLoginFlag = loginSettingsStorage.getAutoLoginFlag()
        view?.setAutoLoginFlag(autoLoginFlag)
    }

    fun saveSettings(token: VKAccessToken, autoLoginFlag: Boolean) {
        (appContext as NewsApplication).userId = token.userId
        appContext.vkToken = token.accessToken
        loginSettingsStorage.saveAutoLoginFlag(autoLoginFlag)
        loginSettingsStorage.saveToken(token.accessToken)
        loginSettingsStorage.saveUserId(token.userId)
        view?.startMainActivity()
    }
}