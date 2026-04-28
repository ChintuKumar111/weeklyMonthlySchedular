package com.shyamdairyfarm.user.data.model.auth.res

data class CustomerLoginRes(
    val data: CustomerLoginData,
    val message: String,
    val status: Boolean
) {
    val isNewCustomer: Boolean
        get() = message.contains("Not a registered customer", ignoreCase = true)
}

data class CustomerLoginData(
    val token: String,
)