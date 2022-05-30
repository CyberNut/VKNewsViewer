package com.example.vknewsviewer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.vknewsviewer.data.Post
import io.reactivex.Maybe

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Post>)

    @Query(value = "SELECT * FROM posts WHERE itsWallPost = 0 ORDER BY date DESC ")
    fun getPosts(): List<Post>

    @Query(value = "SELECT * FROM posts WHERE itsWallPost = 0 ORDER BY date DESC")
    fun getPostsObservable(): Maybe<List<Post>>

    @Query("SELECT * FROM posts WHERE is_liked = 1 AND itsWallPost = 0 ORDER BY date DESC")
    fun getLikedPosts(): List<Post>

    @Query("SELECT * FROM posts WHERE id = :postId AND owner_id = :ownerId")
    fun getPost(ownerId: Int, postId: Int): Post

    @Query(value = "SELECT * FROM posts WHERE itsWallPost = 1 ORDER BY date DESC ")
    fun getWallPosts(): Maybe<List<Post>>

    @Update
    fun updatePost(post: Post)

    @Query("DELETE FROM posts")
    fun deleteAll()

    @Query("DELETE FROM posts WHERE itsWallPost = 0")
    fun deleteAllPosts()

    @Query("DELETE FROM posts WHERE itsWallPost = 1")
    fun deleteAllWallPosts()

    @Query("DELETE FROM posts WHERE id =:id")
    fun delete(id: Int)

    @Transaction
    fun deleteAllPostsAndInsertAll(posts: List<Post>) {
        deleteAllPosts()
        insertAll(posts)
    }

    @Transaction
    fun deleteAllWallPostsAndInsertAll(posts: List<Post>) {
        deleteAllWallPosts()
        insertAll(posts)
    }
}