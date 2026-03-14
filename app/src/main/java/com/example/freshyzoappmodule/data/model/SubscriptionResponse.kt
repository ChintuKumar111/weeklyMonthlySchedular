package com.example.freshyzoappmodule.data.model

data class SubscriptionResponse(
    val productName: String,
    val price: String,
    val deliveryType: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val image: Int
)
