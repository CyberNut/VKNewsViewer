package com.example.vknewsviewer.ui.login

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vknewsviewer.ui.main.MainActivity
import com.example.vknewsviewer.NewsApplication
import com.example.vknewsviewer.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), LoginMVPView {

    @Inject
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NewsApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prepareUI()
        presenter.attachView(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                presenter.saveSettings(token, autoLoginCheckBox.isChecked)
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        presenter.detachView(true)
        super.onDestroy()
    }

    override fun startLoginFlow() {
        VK.login(
            this,
            arrayListOf(
                VKScope.WALL,
                VKScope.FRIENDS,
                VKScope.GROUPS,
                VKScope.PAGES,
                VKScope.OFFLINE
            )
        )
    }

    override fun setAutoLoginFlag(autoLoginFlag: Boolean) {
        autoLoginCheckBox.isChecked = autoLoginFlag
    }

    override fun startMainActivity() {
        MainActivity.startActivity(this)
    }

    private fun prepareUI() {
        loginButton.setOnClickListener {
            presenter.doLogin()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
