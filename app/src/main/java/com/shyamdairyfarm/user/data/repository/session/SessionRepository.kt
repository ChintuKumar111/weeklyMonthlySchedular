package com.shyamdairyfarm.user.data.repository.session

import android.content.Context

// to handle the login navigation and other header authentication based on the api calling

class SessionRepository(private val context: Context) {

    companion object {
        const val PREF_NAME = "AUTH_PREF"
        const val TOKEN = "TOKEN"
        const val PHONE = "PHONE"
        const val NAME = "NAME"
        const val IS_NEW_CUSTOMER = "IS_NEW_CUSTOMER"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun storeToken(token: String?) {
        pref.edit().apply {
            putString(TOKEN, token)
            apply() // async save
        }
    }

    fun storePhoneNumber(phone: String?) {
        pref.edit().apply {
            putString(PHONE, phone)
            apply()
        }
    }

    fun storeName(name: String?) {
        pref.edit().apply {
            putString(NAME, name)
            apply()
        }
    }

    fun getName(): String? {
        return pref.getString(NAME, null)
    }

    fun getPhoneNumber(): String? {
        return pref.getString(PHONE, null)
    }

    fun setAsNewCustomer(value: Boolean = true) {
        pref.edit().apply {
            putBoolean(IS_NEW_CUSTOMER, value)
            apply()
        }
    }

    fun isNewCustomer(): Boolean {
        return pref.getBoolean(IS_NEW_CUSTOMER, true)
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