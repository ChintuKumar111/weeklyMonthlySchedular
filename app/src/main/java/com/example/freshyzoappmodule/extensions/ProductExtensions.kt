package com.example.freshyzoappmodule.extensions

import com.example.freshyzoappmodule.data.model.ProductDetails
import com.example.freshyzoappmodule.data.model.ProductVariant

val ProductDetails.id: Int
    get() = productId.toIntOrNull() ?: 0

val ProductDetails.imageUrl: String
    get() = "https://freshyzo.com/admin/uploads/product_image/$dairyProductImage"

val ProductDetails.price: Int
    get() = productPrice.toDoubleOrNull()?.toInt() ?: 0

val ProductDetails.originalPrice: Int
    get() = dairyMrp.toDoubleOrNull()?.toInt() ?: 0


val ProductVariant.discountPercent: Int
    get() = if (originalPrice > 0)
          ((originalPrice - price) * 100 / originalPrice)
             else 0


val ProductDetails.tag: String
    get() = when {
        productName.contains("Buffalo Milk", true) -> "Rich & Creamy"
        productName.contains("A2 Cow Milk", true) -> "100% Natural"
        productName.contains("Pure Cow Ghee", true) -> "Traditional Recipe"
        productName.contains("Malai Dahi", true) -> "Probiotic"
        productName.contains("Khatti Dahi", true) -> "Classic"
        productName.contains("Fresh Paneer", true) -> "Soft & Fresh"
        productName.contains("Khoya", true) || productName.contains("Khowa", true) -> "Homestyle"
        else -> "100% Natural"
    }

val ProductDetails.badgeText: String
    get() = when {
        productName.contains("A2 Cow Milk", true) -> "A2"
        productName.contains("Pure Cow Ghee", true) -> "PURE"
        productName.contains("Fresh Paneer", true) -> "FRESH"
        else -> ""
    }

val ProductDetails.sizeLabel: String
    get() {
        val words = productName.trim().split(" ")
        return if (words.size > 2)
            "${words[words.size - 2]} ${words.last()}"
        else unit
    }

val ProductDetails.variant: List<ProductVariant>
    get() = listOf(ProductVariant(sizeLabel, price, originalPrice))

val ProductDetails.categoryId: Int
    get() {
        val name = productName.lowercase()

        return when {
            "milk" in name -> 1
            "ghee" in name -> 2
            "dahi" in name -> 3
            "khowa" in name -> 4
            "paneer" in name -> 5
            else -> 0   // important
        }
    }
