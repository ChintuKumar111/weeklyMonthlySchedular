package com.example.freshyzoappmodule.data.model.response

import com.example.freshyzoappmodule.data.model.HomeComboOffers
import com.google.gson.annotations.SerializedName

data class ComboOfferResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<HomeComboOffers>
)