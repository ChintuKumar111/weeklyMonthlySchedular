package com.example.freshyzoappmodule.data.model

data class UserAddress(
    val id: String = "",
    val name: String = "",
    val fullAddress: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val phone: String = "",
    val type: String = "Home", // Home, Work, Other
    val isDefault: Boolean = false
)
