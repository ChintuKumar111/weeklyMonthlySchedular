package com.example.freshyzoappmodule.extensions

import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.ProductSize

val Product.id: Int
    get() = productId.toIntOrNull() ?: 0

val Product.imageUrl: String
    get() = "https://freshyzo.com/admin/uploads/product_image/$dairyProductImage"

val Product.price: Int
    get() = productPrice.toDoubleOrNull()?.toInt() ?: 0

val Product.originalPrice: Int
    get() = dairyMrp.toDoubleOrNull()?.toInt() ?: 0


val ProductSize.discountPercent: Int
    get() = if (originalPrice > 0)
          ((originalPrice - price) * 100 / originalPrice)
             else 0


val Product.tag: String
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

val Product.badgeText: String
    get() = when {
        productName.contains("A2 Cow Milk", true) -> "A2"
        productName.contains("Pure Cow Ghee", true) -> "PURE"
        productName.contains("Fresh Paneer", true) -> "FRESH"
        else -> ""
    }

//val Product.categoryId: Int
//    get() = when {
//        productCategoryName.contains("Ghee", true) -> 2
//        productCategoryName.contains("Milk", true) -> 1
//        productCategoryName.contains("Dahi", true) -> 3
//        productCategoryName.contains("Paneer", true) -> 4
//        productCategoryName.contains("Khowa", true) -> 6
//        else -> 1
//    }

val Product.sizeLabel: String
    get() {
        val words = productName.trim().split(" ")
        return if (words.size > 2)
            "${words[words.size - 2]} ${words.last()}"
        else unit
    }

val Product.sizes: List<ProductSize>
    get() = listOf(ProductSize(sizeLabel, price, originalPrice))

val Product.categoryId: Int
    get() {
        val name = productName.lowercase()

        return when {
            "milk" in name -> 1
            "ghee" in name -> 2
            "dahi" in name -> 3
            "paneer" in name -> 4
            "khoya" in name || "khowa" in name -> 6
            else -> 0   // important
        }
    }