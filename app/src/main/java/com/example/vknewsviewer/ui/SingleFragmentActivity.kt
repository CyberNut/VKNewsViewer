package com.example.vknewsviewer.ui

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vknewsviewer.R

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragmentСontainer)

        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragmentСontainer, fragment)
                .commit()
        }
    }

    protected open fun replaceFragment(
        fragment: Fragment,
        backStackString: String?,
        @AnimRes enterAnim: Int = R.anim.slide_in_top,
        @AnimRes exitAnim: Int = R.anim.slide_out_top
    ) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        backStackString?.let { fragmentTransaction.addToBackStack(it) }
        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragmentСontainer, fragment)
            .commit()
    }
}