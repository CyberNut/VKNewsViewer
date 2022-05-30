package com.example.vknewsviewer.di

import android.content.Context
import com.example.vknewsviewer.ui.login.LoginActivity
import com.example.vknewsviewer.ui.splashscreen.SplashScreenActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class, NetworkModule::class, AppSubcomponents::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder
        fun networkModule(networkModule: NetworkModule): Builder
        fun build(): AppComponent
    }

    fun mainActivityComponent(): MainActivityComponent.Factory
    fun inject(loginActivity: LoginActivity)
    fun inject(splashScreenActivity: SplashScreenActivity)
}