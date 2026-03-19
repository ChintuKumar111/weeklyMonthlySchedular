package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    val url: String,
    val type: MediaType
) : Parcelable


data class ProductMedia(
    val url: String,
    val isVideo: Boolean = false
)
enum class MediaType {
    IMAGE,
    VIDEO
}