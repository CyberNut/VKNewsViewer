package com.example.vknewsviewer.network.responses

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("can_access_closed") val canAccessClosed: Boolean,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("id") val id: Int,
    @SerializedName("is_closed") val isClosed: Boolean,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("photo_200") val photo200: String
)

data class UserDataResponse(
    @SerializedName("can_access_closed") val canAccessClosed: Boolean,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("id") val id: Int,
    @SerializedName("is_closed") val isClosed: Boolean,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("photo") val photo: String,
    @SerializedName("photo_100") val photo100: String,
    @SerializedName("domain") val domain: String,
    @SerializedName("online") val online: Int,
    @SerializedName("bdate") val birthDate: String?,
    @SerializedName("city") val city: VkCity?,
    @SerializedName("country") val country: VkCountry?,
    @SerializedName("last_seen") val lastSeen: VkLastSeen?,
    @SerializedName("followers_count") val followersCount: Int,
    @SerializedName("university_name") val universityName: String?,
    @SerializedName("career") val career: List<VkCareer>?,
    @SerializedName("about") val about: String?,
    @SerializedName("faculty_name") val facultyName: String?
)

data class VkCity(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String
)

data class VkCountry(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String
)

data class VkLastSeen(
    @SerializedName("platform") val platform: Int,
    @SerializedName("time") val time: Long
)

data class VkCareer(
    @SerializedName("company") val company: String?,
    @SerializedName("position") val position: String?
)
