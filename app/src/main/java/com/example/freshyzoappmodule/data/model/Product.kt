package com.example.freshyzoappmodule.data.model

data class Product(
    val id: Int,
    val name: String,
    val tag: String,          // e.g. "100% Natural"
    val description: String,
    val imageRes: Int,        // drawable resource id
    val badgeText: String,    // e.g. "A2", "NEW", "" for none
    val sizes: List<ProductSize>,
    val categoryId: Int,
    val hasVip: Boolean = false,
    val vipSavingText: String = ""
)

data class ProductSize(
    val label: String,        // e.g. "500ml", "1 Litre", "200gm"
    val price: Int,
    val originalPrice: Int
) {
    val discountPercent: Int
        get() = if (originalPrice > 0)
            ((originalPrice - price) * 100 / originalPrice)
        else 0
}