package com.example.freshyzoappmodule.data.model.response

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("type") val type: String?
)