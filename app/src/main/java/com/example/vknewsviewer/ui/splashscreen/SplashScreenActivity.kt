package com.example.vknewsviewer.ui.splashscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.NewsApplication
import com.example.vknewsviewer.R
import com.example.vknewsviewer.ui.login.LoginActivity
import javax.inject.Inject

class SplashScreenActivity : AppCompatActivity(), SplashMVPView {

    @Inject
    lateinit var presenter: SplashScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NewsApplication).appComponent.inject(this)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView(true)
    }

    override fun openMainActivity() {
        MainActivity.startActivity(this)
        finish()
    }

    override fun openLoginActivity() {
        LoginActivity.startActivity(this)
        finish()
    }
}