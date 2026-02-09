package com.example.freshyzoappmodule.data.model

// Create this in your model package
data class CartStateModel(
    val itemsCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isVisible: Boolean = false
)