package com.example.freshyzoappmodule.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.freshyzoappmodule.data.model.CartStateModel
import com.google.gson.Gson

class CartRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCartState(cartState: CartStateModel) {
        val cartStateJson = gson.toJson(cartState)
        sharedPreferences.edit().putString("cart_state", cartStateJson).apply()
    }

    fun getCartState(): CartStateModel? {
        val cartStateJson = sharedPreferences.getString("cart_state", null)
        return if (cartStateJson != null) {
            gson.fromJson(cartStateJson, CartStateModel::class.java)
        } else {
            null
        }
    }
}
