package com.example.freshyzoappmodule.data.model

data class CartUiState(
    val itemsCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isVisible: Boolean = false,
    val productQuantities: Map<Int, Int> = emptyMap(),
    val productDetails: List<ProductDetails> = emptyList(),
    val discount: Double = 0.0
)
