package com.shyamdairyfarm.user.data.model.auth.res

data class OtpRes(
    val `data`: List<Any>,
    val message: String,
    val status: Boolean
)