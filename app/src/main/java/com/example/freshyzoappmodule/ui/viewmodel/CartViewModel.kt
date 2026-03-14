package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.CartState

class CartViewModel : ViewModel() {

    // Internal mutable state
    private val _cartState = MutableLiveData<CartState>(CartState())

    // External immutable LiveData for the View to observe
    val cartState: LiveData<CartState> get() = _cartState

    fun addItemToCart(price: Double) {
        val currentState = _cartState.value ?: CartState()
        val newCount = currentState.itemsCount + 1
        val newPrice = currentState.totalPrice + price

        _cartState.value = currentState.copy(
            itemsCount = newCount,
            totalPrice = newPrice,
            isVisible = true
        )
    }

    fun hideCart() {
        _cartState.value = _cartState.value?.copy(isVisible = false)
    }

    fun toggleCartVisibility() {
        val current = _cartState.value?.isVisible ?: false
        _cartState.value = _cartState.value?.copy(isVisible = !current)
    }
}