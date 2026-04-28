package com.shyamdairyfarm.user.data.model.auth.res

data class RegisterNewCustomerRes(
    val `data`: RegisterNewCustomerData,
    val message: String,
    val status: Boolean
)

data class RegisterNewCustomerData(
    val customer_id: Int,
    val delivery_available: Boolean,
    val token :String? = null
)