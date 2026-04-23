package com.shyamdairyfarm.user.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeComboOffers(
    @SerializedName("combo_id") val comboId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: String,
    @SerializedName("image_url") val imageUrl: String
) : Parcelable
