package com.example.vknewsviewer.utils

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginSettingsStorage @Inject constructor(context: Context) {

    private val PREF_FILE_NAME = "Login"
    private val PREF_AUTOLOGIN_FLAG = "AUTOLOGIN_FLAG"
    private val PREF_TOKEN = "TOKEN"
    private val PREF_USER_ID = "USER_ID"
    private val sharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    private var autoLoginFlag: Boolean
    private var token: String
    private var userId: Int

    init {
        autoLoginFlag = getAutoLoginFlag()
        token = getToken()
        userId = getUserId()
    }

    fun saveAutoLoginFlag(isAutoLogin: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_AUTOLOGIN_FLAG, isAutoLogin)
            .apply()
    }

    fun getAutoLoginFlag(): Boolean {
        return sharedPreferences.getBoolean(PREF_AUTOLOGIN_FLAG, false)
    }

    fun saveUserId(id: Int) {
        sharedPreferences.edit()
            .putInt(PREF_USER_ID, id)
            .apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(PREF_USER_ID, 0)
    }

    fun saveToken(newToken: String) {
        sharedPreferences.edit()
            .putString(PREF_TOKEN, newToken)
            .apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString(PREF_TOKEN, EMPTY_STRING) ?: EMPTY_STRING
    }
}