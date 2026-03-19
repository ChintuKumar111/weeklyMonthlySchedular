package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.CartUiState

class CartViewModel : ViewModel() {

    // Internal mutable state
    private val _cartUiState = MutableLiveData<CartUiState>(CartUiState())

    // External immutable LiveData for the View to observe
    val cartUiState: LiveData<CartUiState> get() = _cartUiState

    fun addItemToCart(price: Double) {
        val currentState = _cartUiState.value ?: CartUiState()
        val newCount = currentState.itemsCount + 1
        val newPrice = currentState.totalPrice + price

        _cartUiState.value = currentState.copy(
            itemsCount = newCount,
            totalPrice = newPrice,
            isVisible = true
        )
    }

    fun hideCart() {
        _cartUiState.value = _cartUiState.value?.copy(isVisible = false)
    }

    fun toggleCartVisibility() {
        val current = _cartUiState.value?.isVisible ?: false
        _cartUiState.value = _cartUiState.value?.copy(isVisible = !current)
    }
}