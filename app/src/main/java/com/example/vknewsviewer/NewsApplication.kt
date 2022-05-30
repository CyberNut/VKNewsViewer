package com.example.vknewsviewer

import android.app.Application
import android.util.Log
import com.example.vknewsviewer.di.AppComponent
import com.example.vknewsviewer.di.DaggerAppComponent
import com.example.vknewsviewer.di.NetworkModule
import com.example.vknewsviewer.ui.login.LoginActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class NewsApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .bindContext(applicationContext)
            .networkModule(NetworkModule())
            .build()
    }

    var vkToken = ""
    var userId: Int = 0

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            Log.d("NewsApplication", "onTokenExpired")
            vkToken = ""
            LoginActivity.startActivity(applicationContext)
        }
    }
}