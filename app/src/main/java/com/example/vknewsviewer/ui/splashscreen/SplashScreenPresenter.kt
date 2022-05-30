package com.example.vknewsviewer.ui.splashscreen

import android.util.Log
import com.example.vknewsviewer.network.VkApiService
import com.example.vknewsviewer.utils.LoginSettingsStorage
import com.example.vknewsviewer.ui.basemvp.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SplashScreenPresenter @Inject constructor(
    private val loginSettingsStorage: LoginSettingsStorage,
    private val vkApiService: VkApiService
) : BasePresenter<SplashMVPView>() {

    private val TAG = "SplashScreenPresenter"

    override fun attachView(view: SplashMVPView) {
        super.attachView(view)
        decideActivityToOpen()
    }

    private fun decideActivityToOpen() {
        if (loginSettingsStorage.getAutoLoginFlag()) {
            val token = loginSettingsStorage.getToken()
            if (token.isEmpty()) {
                view?.openLoginActivity()
            } else {
                compositeDisposable.add(
                    vkApiService.getAccountLanguageCode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(onError = { error ->
                            Log.e(TAG, "Checking token error: ${error.localizedMessage}", error)
                            view?.openMainActivity()
                        }, onSuccess = { checkTokenResponse ->
                            if (checkTokenResponse.hasError()) {
                                view?.openLoginActivity()
                            } else {
                                view?.openMainActivity()
                            }
                        })
                )
            }
        } else {
            view?.openLoginActivity()
        }
    }
}
