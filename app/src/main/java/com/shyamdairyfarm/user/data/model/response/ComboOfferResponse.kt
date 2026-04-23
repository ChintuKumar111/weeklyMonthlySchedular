package com.shyamdairyfarm.user.data.model.response

import com.shyamdairyfarm.user.data.model.HomeComboOffers
import com.google.gson.annotations.SerializedName

data class ComboOfferResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<HomeComboOffers>
)