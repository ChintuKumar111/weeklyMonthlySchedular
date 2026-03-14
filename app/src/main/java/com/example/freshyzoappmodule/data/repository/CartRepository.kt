package com.example.freshyzoappmodule.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.freshyzoappmodule.data.model.CartState
import com.google.gson.Gson

class CartRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCartState(cartState: CartState) {
        val cartStateJson = gson.toJson(cartState)
        sharedPreferences.edit().putString("cart_state", cartStateJson).apply()
    }
    fun getCartState(): CartState? {
        val cartStateJson = sharedPreferences.getString("cart_state", null)
        return if (cartStateJson != null) {
            gson.fromJson(cartStateJson, CartState::class.java)
        } else {
            null
        }
    }
}
