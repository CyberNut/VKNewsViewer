package com.example.vknewsviewer.ui.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.vknewsviewer.NewsApplication
import com.example.vknewsviewer.R
import com.example.vknewsviewer.di.MainActivityComponent
import com.example.vknewsviewer.ui.BottomNavBarVisibleHelper
import com.example.vknewsviewer.ui.SingleFragmentActivity
import com.example.vknewsviewer.ui.favoriteslist.FavoritesListFragment
import com.example.vknewsviewer.ui.newslist.NewsListFragment
import com.example.vknewsviewer.ui.post.PostFragment
import com.example.vknewsviewer.ui.userprofile.UserProfileFragment
import com.example.vknewsviewer.utils.LoginSettingsStorage
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

const val NEWS_POST_FRAGMENT_TAG = "NEWS_POST_FRAGMENT"
const val USER_PROFILE_FRAGMENT_TAG = "USER_PROFILE_FRAGMENT_TAG"

class MainActivity : SingleFragmentActivity(), BottomNavBarVisibleHelper {

    lateinit var mainActivityComponent: MainActivityComponent

    @Inject
    lateinit var loginSettingsStorage: LoginSettingsStorage

    @Inject
    lateinit var viewModel: MainActivityViewModel

    override fun createFragment(): Fragment {
        return NewsListFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent =
            (application as NewsApplication).appComponent.mainActivityComponent().create()
        mainActivityComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as NewsApplication).userId = loginSettingsStorage.getUserId()
        prepareUI()
        registerNetworkCallback()
    }

    override fun setBottomNavBarVisibility(visible: Boolean) {
        if (visible) {
            bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun setFavoritesTabVisibility(isLikedPostListEmpty: Boolean) {
        bottomNavigationView.menu.findItem(R.id.action_favorites_list).isVisible =
            !isLikedPostListEmpty
        if (isLikedPostListEmpty && bottomNavigationView.selectedItemId == R.id.action_favorites_list) {
            bottomNavigationView.selectedItemId = R.id.action_news_list
            showNewsListFragment()
        }
    }

    fun showNewsPostFragment(ownerId: Int, postId: Int) {
        replaceFragment(
            PostFragment.newInstance(ownerId, postId),
            NEWS_POST_FRAGMENT_TAG,
            R.anim.fade_in,
            R.anim.fade_out
        )
    }

    private fun prepareUI() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                bottomNavigationView.selectedItemId -> {
                    true
                }
                R.id.action_news_list -> {
                    showNewsListFragment()
                    true
                }
                R.id.action_favorites_list -> {
                    showFavoritesListFragment()
                    true
                }
                R.id.action_user_profile -> {
                    showUserProfileFragment()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    fun showUserProfileFragment(id: Int = 0) {
        var userId = id
        var backStackTag: String? = USER_PROFILE_FRAGMENT_TAG
        if (userId == 0) {
            userId = (application as NewsApplication).userId
            backStackTag = null
        }
        replaceFragment(
            UserProfileFragment.newInstance(userId),
            backStackTag,
            R.anim.fade_in,
            R.anim.fade_out
        )
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun showNewsListFragment() {
        replaceFragment(NewsListFragment.newInstance(), null)
    }

    private fun showFavoritesListFragment() {
        replaceFragment(FavoritesListFragment.newInstance(), null)
    }

    private fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                }

                override fun onLost(network: Network) {
                    showLostConnectionSnackbar()
                }
            })
        }
    }

    private fun showLostConnectionSnackbar() {
        Snackbar.make(
            fragmentСontainer,
            getString(R.string.network_not_available),
            Snackbar.LENGTH_INDEFINITE
        )
            .setActionTextColor(getColor(R.color.colorButton))
            .setBackgroundTint(getColor(R.color.colorWarning))
            .setAnchorView(bottomNavigationView)
            .setAction(R.string.hide_button) { }
            .show()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}