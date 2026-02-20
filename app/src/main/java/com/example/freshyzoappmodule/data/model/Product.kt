package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("product_id") val productId: String,
    @SerializedName("product_name") val productName: String,
    @SerializedName("dairy_product_image") val dairyProductImage: String,
    @SerializedName("short_desc") val shortDesc: String,
    @SerializedName("description") val description: String,
    @SerializedName("nutri_val") val nutriVal: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("product_price") val productPrice: String,
    @SerializedName("dairy_mrp") val dairyMrp: String,
    @SerializedName("product_category_name") val productCategoryName: String,
    var quantity: Int = 0
) : Parcelable {

    val id: Int get() = productId.toIntOrNull() ?: 0

    val imageUrl: String get() = "https://freshyzo.com/admin/uploads/product_image/$dairyProductImage"

    val tag: String get() = when {
        productName.contains("Buffalo Milk", true) -> "Rich & Creamy"
        productName.contains("A2 Cow Milk", true) -> "100% Natural"
        productName.contains("Pure Cow Ghee", true) -> "Traditional Recipe"
        productName.contains("Malai Dahi", true) -> "Probiotic"
        productName.contains("Khatti Dahi", true) -> "Classic"
        productName.contains("Fresh Paneer", true) -> "Soft & Fresh"
        productName.contains("Khoya", true) || productName.contains("Khowa", true) -> "Homestyle"
        else -> "100% Natural"
    }

    val badgeText: String get() = when {
        productName.contains("A2 Cow Milk", true) -> "A2"
        productName.contains("Pure Cow Ghee", true) -> "PURE"
        productName.contains("Fresh Paneer", true) -> "FRESH"
        else -> ""
    }

    val categoryId: Int get() = when {
        productName.contains("Ghee", true) -> 2
        productName.contains("Milk", true) -> 1
        productName.contains("Dahi", true) -> 3
        productName.contains("Paneer", true) -> 4
        productName.contains("Khowa", true) || productName.contains("Khoya", true) -> 6
        else -> 1
    }

    val sizeLabel: String get() {
        val words = productName.trim().split(" ")
        return if (words.size > 2) "${words[words.size - 2]} ${words.last()}" else unit
    }

    val price: Int get() = productPrice.toDoubleOrNull()?.toInt() ?: 0
    val originalPrice: Int get() = dairyMrp.toDoubleOrNull()?.toInt() ?: 0
    
    val discountPercent: Int get() = if (originalPrice > 0) ((originalPrice - price) * 100 / originalPrice) else 0

    // Compatibility property for Adapter that expects a list of sizes
    val sizes: List<ProductSize> get() = listOf(ProductSize(sizeLabel, price, originalPrice))
    
    val name: String get() = productName
    val short_description: String get() = shortDesc
}

@Parcelize
data class ProductSize(
    val label: String,
    val price: Int,
    val originalPrice: Int
) : Parcelable {
    val discountPercent: Int
        get() = if (originalPrice > 0)
            ((originalPrice - price) * 100 / originalPrice)
        else 0
}
