package com.example.freshyzoappmodule.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val product_id: String,
    val product_name: String,
    val dairy_product_image: String,
    val short_desc: String,
    val description: String,
    val nutri_val: String,
    val unit: String,
    val product_price: String,
    val dairy_mrp: String,
    val product_category_name: String
) : Parcelable