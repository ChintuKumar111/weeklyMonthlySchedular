package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDetails(
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
) : Parcelable
