package com.example.freshyzoappmodule.data.manager

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "freshyzo_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_PROFILE_IMAGE = "profile_image_uri"
    private const val KEY_USER_DOB = "user_dob"
    private const val KEY_LANGUAGE = "app_language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserName(context: Context, name: String) {
        getPrefs(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_NAME, "User Name")
    }

    fun saveProfileImage(context: Context, uri: String) {
        getPrefs(context).edit().putString(KEY_PROFILE_IMAGE, uri).apply()
    }

    fun getProfileImage(context: Context): String? {
        return getPrefs(context).getString(KEY_PROFILE_IMAGE, null)
    }

    fun saveUserDob(context: Context, dob: String) {
        getPrefs(context).edit().putString(KEY_USER_DOB, dob).apply()
    }

    fun getUserDob(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_DOB, null)
    }

    fun saveLanguage(context: Context, lang: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, lang).apply()
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun clearUserData(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}