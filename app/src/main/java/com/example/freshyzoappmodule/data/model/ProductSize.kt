package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ProductSize(
    val label: String,
    val price: Int,
    val originalPrice: Int
) : Parcelable