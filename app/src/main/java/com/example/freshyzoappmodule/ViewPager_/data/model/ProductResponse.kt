package com.example.freshyzoappmodule.ViewPager_.data.model

data class ProductResponse(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val images: List<String>
)

