package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderHistoryModel(
    val id: Int,
    val txnId: String,
    val productName: String,
    val brandName: String,
    val emoji: String,
    val productType: ProductType,
    val size: String,
    val quantity: Int,
    val amountPaid: Double,
    val date: String,
    val status: DeliveryStatus
): Parcelable

@Parcelize
enum class DeliveryStatus : Parcelable {
    PLACED,
    PENDING,
    CANCELLED
}

@Parcelize
enum class ProductType : Parcelable {
    MILK,
    GHEE
}
