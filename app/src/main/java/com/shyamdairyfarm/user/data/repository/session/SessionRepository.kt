package com.shyamdairyfarm.user.data.repository.session

import android.content.Context

// to handle the login navigation and other header authentication based on the api calling

class SessionRepository(private val context: Context) {

    companion object {
        const val PREF_NAME = "AUTH_PREF"
        const val TOKEN = "TOKEN"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun storeToken(token: String?) {
        pref.edit().apply {
            putString(TOKEN, token)
            apply() // async save
        }
    }

    fun getToken(): String? {
        return pref.getString(TOKEN, null)
    }

    fun clearSession() {
        pref.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}