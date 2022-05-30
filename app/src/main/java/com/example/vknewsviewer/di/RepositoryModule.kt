package com.example.vknewsviewer.di

import android.content.Context
import androidx.room.Room
import com.example.vknewsviewer.db.PostDao
import com.example.vknewsviewer.db.PostDatabase
import com.example.vknewsviewer.db.UserProfileDao
import com.example.vknewsviewer.network.VkApiService
import com.example.vknewsviewer.repository.PostsRepository
import com.example.vknewsviewer.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        vkService: VkApiService,
        postDao: PostDao,
        userProfileDao: UserProfileDao
    ): Repository {
        return PostsRepository(
            vkService,
            postDao,
            userProfileDao
        )
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): PostDatabase {
        return Room.databaseBuilder(context, PostDatabase::class.java, "posts.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providePostDao(postDatabase: PostDatabase): PostDao {
        return postDatabase.postDao()
    }

    @Singleton
    @Provides
    fun provideUserProfileDao(postDatabase: PostDatabase): UserProfileDao {
        return postDatabase.userProfileDao()
    }
}