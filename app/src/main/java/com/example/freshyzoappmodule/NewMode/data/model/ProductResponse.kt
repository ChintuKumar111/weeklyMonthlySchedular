package com.example.freshyzoappmodule.NewMode.data.model

data class ProductResponse(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val images: List<String>
)

