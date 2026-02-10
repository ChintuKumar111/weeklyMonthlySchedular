package com.example.freshyzoappmodule.ViewPager_.data.model

data class ProductListResponse(
    val products: List<ProductResponse>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
