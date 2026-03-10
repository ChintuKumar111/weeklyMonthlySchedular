package com.example.freshyzoappmodule.data.model


data class DeliveryModel(
    val id: Int,
    val txnId: String,
    val productName: String,
    val brandName: String,
    val emoji: String,
    val productType: ProductType,
    val size: String,
    val quantity: Int,
    val amountPaid: Double,
    val remainingBalance: Double,
    val date: String,
    val status: DeliveryStatus
)

enum class DeliveryStatus {
    PLACED,
    PENDING,
    CANCELLED
}

enum class ProductType {
    MILK,
    GHEE
}