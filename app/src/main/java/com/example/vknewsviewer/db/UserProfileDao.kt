package com.example.vknewsviewer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.vknewsviewer.data.VkUserProfile
import io.reactivex.Maybe

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProfile: VkUserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userProfiles: List<VkUserProfile>)

    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserProfile(id: Int): Maybe<VkUserProfile>

    @Update
    fun updateUserProfile(userProfile: VkUserProfile)

    @Query("DELETE FROM user_profile")
    fun deleteAll()

    @Query("DELETE FROM user_profile WHERE id =:id")
    fun delete(id: Int)

    @Transaction
    fun deleteAllAndInsertAll(userProfiles: List<VkUserProfile>) {
        deleteAll()
        insertAll(userProfiles)
    }
}