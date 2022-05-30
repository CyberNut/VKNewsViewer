package com.example.vknewsviewer.repository

import com.example.vknewsviewer.data.Comment
import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.data.VkUser
import com.example.vknewsviewer.data.VkUserProfile
import com.example.vknewsviewer.db.PostDao
import com.example.vknewsviewer.db.UserProfileDao
import com.example.vknewsviewer.network.VkApiResponse
import com.example.vknewsviewer.network.VkApiService
import com.example.vknewsviewer.network.responses.CommentItem
import com.example.vknewsviewer.network.responses.UserDataResponse
import com.example.vknewsviewer.network.responses.VkLastSeen
import com.example.vknewsviewer.network.responses.WallItem
import com.example.vknewsviewer.network.responses.WallPostResponse
import com.example.vknewsviewer.utils.EMPTY_STRING
import com.example.vknewsviewer.utils.THOUSAND
import com.example.vknewsviewer.utils.UNDERSCORE_STRING
import com.example.vknewsviewer.utils.toBoolean
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepository @Inject constructor(
    private val vkService: VkApiService,
    private val postDao: PostDao,
    private val userProfileDao: UserProfileDao
) :
    Repository {

    override fun getPosts(forceUpdate: Boolean): Observable<List<Post>> {
        val postsFromDb = postDao.getPostsObservable().toObservable()
        return if (forceUpdate) {
            Observable.concat(
                postsFromDb,
                Observable.fromCallable { getPostsFromNetwork() }
            )
        } else {
            postsFromDb
        }
    }

    override fun getWallPosts(userId: Int, usingCache: Boolean): Observable<List<Post>> {
        return if (usingCache) {
            val postsFromDb = postDao.getWallPosts().toObservable()
            Observable.concat(
                postsFromDb,
                Observable.fromCallable { getWallPostsFromNetwork(userId, usingCache) }
                    .onErrorResumeNext(postsFromDb)
            )
        } else {
            Observable.fromCallable { getWallPostsFromNetwork(userId, usingCache) }
        }
    }

    override fun getComments(post: Post): Single<List<Comment>> {
        return Single.fromCallable {
            val comments = mutableListOf<Comment>()
            val vkCommentResponse = vkService.getComments(post.id, post.ownerId).execute()
            if (vkCommentResponse.isSuccessful) {
                vkCommentResponse.body()?.let { commentResponse ->
                    val commentItems = commentResponse.response.items
                    if (commentItems != null) {
                        val users = getUsersFromComments(commentItems)
                        commentItems.forEach { commentItem ->
                            val user = users[commentItem.fromId]
                            if (user != null) {
                                comments.add(
                                    Comment(
                                        commentItem.id,
                                        commentItem.text,
                                        user,
                                        commentItem.date * THOUSAND
                                    )
                                )
                            }
                        }
                    }
                }
            }
            comments
        }
    }

    override fun likePost(ownerId: Int, postId: Int): Maybe<Pair<Int, Int>> {
        return Maybe.fromCallable {
            val post = getPostNow(ownerId, postId)
            post?.let {
                val likeApiResponse = if (!post.isLiked) {
                    vkService.likePost(post.id, post.ownerId).execute().body()
                } else {
                    vkService.unlikePost(post.id, post.ownerId).execute().body()
                }
                val likes = likeApiResponse!!.response.likes
                post.like(likes)
                postDao.updatePost(post)
                Pair(post.id, likes)
            }
        }
    }

    override fun getLikedPosts() = Single.fromCallable { postDao.getLikedPosts() }

    override fun getPost(ownerId: Int, postId: Int): Single<Post> {
        return Single.fromCallable {
            getPostNow(ownerId, postId)
        }
    }

    override fun deletePost(id: Int) = Completable.fromCallable { postDao.delete(id) }

    override fun sendComment(post: Post, commentText: String): Completable {
        return Completable.fromCallable {
            vkService.sendComment(post.ownerId, post.id, commentText).execute()
        }
    }

    override fun sendWallPost(userId: Int, commentText: String): Completable {
        return Completable.fromCallable {
            vkService.sendWallPost(userId, commentText).execute()
        }
    }

    override fun getUserProfile(
        userId: Int,
        usingCache: Boolean,
        forceUpdate: Boolean
    ): Observable<VkUserProfile> {
        return if (usingCache) {
            val userProfileFromDb = userProfileDao.getUserProfile(userId).toObservable()
            if (forceUpdate) {
                Observable.concat(
                    userProfileFromDb,
                    Observable.fromCallable {
                        getUserProfileFromNetwork(userId, usingCache)
                    }
                )
            } else {
                userProfileFromDb
            }
        } else {
            Observable.fromCallable {
                getUserProfileFromNetwork(userId, usingCache)
            }
        }
    }

    private fun getPostNow(ownerId: Int, postId: Int): Post? {
        var post: Post? = postDao.getPost(ownerId, postId)
        if (post == null) {
            post = getWallPostByIds(ownerId, postId)
        }
        return post
    }

    private fun getWallPostByIds(ownerId: Int, postId: Int): Post? {
        val queryString = ownerId.toString() + UNDERSCORE_STRING + postId.toString()
        val vkResponse = vkService.getWallPostByIds(queryString).execute()
        if (vkResponse.isSuccessful) {
            vkResponse.body()?.let { postApiResponse ->
                return getPostsFromVkWallPostsResponse(postApiResponse.response).first()
            }
        }
        return null
    }

    private fun getUserProfileFromNetwork(userId: Int, usingCache: Boolean): VkUserProfile? {
        var userProfile: VkUserProfile? = null
        val userProfileResponse = vkService.getUserProfileData(userId.toString()).execute()
        if (userProfileResponse.isSuccessful) {
            userProfileResponse.body()?.let { userListResponse ->
                val response = userListResponse.response.first()
                userProfile = VkUserProfile(
                    userId,
                    response.firstName + " " + response.lastName,
                    response.photo,
                    response.birthDate,
                    getCityInfo(response),
                    response.universityName,
                    response.online.toBoolean(),
                    response.domain,
                    getLastSeenDate(response.lastSeen),
                    getCareerInfo(response),
                    response.about,
                    response.followersCount
                )
            }
        }
        if (usingCache && userProfile != null) {
            userProfileDao.insert(userProfile!!)
        }
        return userProfile
    }

    private fun getLastSeenDate(lastSeen: VkLastSeen?): Long {
        var result = 0L
        if (lastSeen != null) {
            result = lastSeen.time * THOUSAND
        }
        return result
    }

    private fun getCareerInfo(response: UserDataResponse): String {
        var result = EMPTY_STRING
        response.career?.let { careerList ->
            if (careerList.isNotEmpty()) {
                careerList.last()?.let { job ->
                    if (!job.position.isNullOrEmpty()) {
                        result = job.position
                    }
                    if (!job.company.isNullOrEmpty()) {
                        result += if (result.isNotEmpty()) {
                            " (" + job.company + ")"
                        } else {
                            job.company
                        }
                    }
                }
            }
        }
        return result
    }

    private fun getCityInfo(response: UserDataResponse): String {
        var result = EMPTY_STRING
        response.city?.let { city ->
            result = city.title
        }
        response.country?.let { country ->
            if (result.isNotEmpty()) {
                result += ", " + country.title
            } else {
                result = country.title
            }
        }
        return result
    }

    private fun getWallPostsFromNetwork(userId: Int, usingCache: Boolean): List<Post> {
        val list = mutableListOf<Post>()
        val vkResponse = vkService.getWallPosts(userId).execute()
        if (vkResponse.isSuccessful) {
            vkResponse.body()?.let { postApiResponse ->
                list.addAll(getPostsFromVkWallPostsResponse(postApiResponse))
                if (usingCache) {
                    postDao.deleteAllWallPostsAndInsertAll(list)
                }
            }
        }
        return list
    }

    private fun getUsersFromComments(comments: List<CommentItem>): Map<Int, VkUser> {
        val userIds = mutableListOf<String>()
        comments.forEach { comment -> userIds.add(comment.fromId.toString()) }
        val idsString = userIds.joinToString(",")
        return getUsersByIds(idsString)
    }

    private fun getUsersByIds(idsString: String): Map<Int, VkUser> {
        val users = mutableMapOf<Int, VkUser>()
        val getUsersResponse = vkService.getUsers(idsString).execute()
        if (getUsersResponse.isSuccessful) {
            getUsersResponse.body()?.let { vkGetUsersResponse ->
                vkGetUsersResponse.response.forEach { userResponse ->
                    users[userResponse.id] = VkUser(
                        userResponse.id,
                        userResponse.firstName,
                        userResponse.lastName,
                        userResponse.photo200
                    )
                }
            }
        }
        return users
    }

    private fun getPostsFromNetwork(): List<Post> {
        val list = mutableListOf<Post>()
        val vkResponse = vkService.getPosts().execute()
        if (vkResponse.isSuccessful) {
            vkResponse.body()?.let { postApiResponse ->
                list.addAll(postApiResponse.response.items)
                postDao.deleteAllPostsAndInsertAll(list)
            }
        }
        return list
    }

    private fun getPostsFromVkWallPostsResponse(postApiResponse: VkApiResponse<WallPostResponse>): MutableList<Post> {
        val postList = mutableListOf<Post>()
        if (postApiResponse.hasError()) {
            throw Exception(postApiResponse.error?.errorMessage)
        }
        val postsItems = postApiResponse.response.items
        getPostsFromWallItemList(postsItems, postList)
        return postList
    }

    private fun getPostsFromVkWallPostsResponse(items: List<WallItem>?): MutableList<Post> {
        val postList = mutableListOf<Post>()
        if (items != null) {
            getPostsFromWallItemList(items, postList)
        }
        return postList
    }

    private fun getPostsFromWallItemList(
        postsItems: List<WallItem>?,
        postList: MutableList<Post>
    ) {
        postsItems?.let { items ->
            val users = getUsersFromWallPosts(items)
            items.map { item ->
                val imageUrl = getItemImageUrl(item)
                if (imageUrl.isNotEmpty() || item.text.isNotEmpty()) {
                    postList.add(
                        Post(
                            item.id,
                            item.from_id,
                            item.owner_id,
                            item.date * THOUSAND,
                            item.likes.count,
                            item.likes.userLikes.toBoolean(),
                            (users[item.from_id] ?: error("")).getUserName(),
                            item.comments.count,
                            item.comments.canPost.toBoolean(),
                            item.text,
                            users[item.from_id]!!.photoImageUrl,
                            imageUrl,
                            true
                        )
                    )
                }
            }
        }
    }

    private fun getUsersFromWallPosts(wallPosts: List<WallItem>): Map<Int, VkUser> {
        val userIds = mutableListOf<String>()
        wallPosts.forEach { post -> userIds.add(post.from_id.toString()) }
        val idsString = userIds.joinToString(",")
        return getUsersByIds(idsString)
    }

    private fun getItemImageUrl(item: WallItem): String {
        item.attachments?.map { attachment ->
            return when (attachment.type) {
                ATTACHMENT_TYPE_PHOTO -> {
                    if (attachment.photos?.photos?.last()?.url != null) {
                        attachment.photos.photos.last().url
                    } else {
                        EMPTY_STRING
                    }
                }
                ATTACHMENT_TYPE_VIDEO -> {
                    if (attachment.video?.images != null) {
                        attachment.video.images.last().url
                    } else {
                        EMPTY_STRING
                    }
                }
                ATTACHMENT_TYPE_LINK -> {
                    if (attachment.link?.photo?.url != null) {
                        attachment.link.photo.url
                    } else {
                        EMPTY_STRING
                    }
                }
                else -> EMPTY_STRING
            }
        }
        return EMPTY_STRING
    }

    companion object {
        const val ATTACHMENT_TYPE_PHOTO = "photo"
        const val ATTACHMENT_TYPE_VIDEO = "video"
        const val ATTACHMENT_TYPE_LINK = "link"
    }
}