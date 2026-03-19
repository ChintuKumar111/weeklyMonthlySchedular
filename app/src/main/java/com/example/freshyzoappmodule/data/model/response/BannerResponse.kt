package com.example.freshyzoappmodule.data.model.response

data class BannerResponse(
    val status: Boolean,
    val data: List<Banner>

)

data class Banner(
    val id: Int,
    val image: String
)