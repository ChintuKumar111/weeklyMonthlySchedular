package com.example.freshyzoappmodule.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.freshyzoappmodule.data.model.CartUiState
import com.google.gson.Gson

class CartRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCartState(cartUiState: CartUiState) {
        val cartStateJson = gson.toJson(cartUiState)
        sharedPreferences.edit().putString("cart_state", cartStateJson).apply()
    }
    fun getCartState(): CartUiState? {
        val cartStateJson = sharedPreferences.getString("cart_state", null)
        return if (cartStateJson != null) {
            gson.fromJson(cartStateJson, CartUiState::class.java)
        } else {
            null
        }
    }

    fun clearCart() {
        sharedPreferences.edit().remove("cart_state").apply()
    }
}
