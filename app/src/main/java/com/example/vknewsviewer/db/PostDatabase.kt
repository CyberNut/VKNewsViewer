package com.example.vknewsviewer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUserProfile

@Database(entities = [Post::class, VkUserProfile::class], version = 1, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userProfileDao(): UserProfileDao
}