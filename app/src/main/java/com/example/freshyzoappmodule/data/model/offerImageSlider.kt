package com.example.freshyzoappmodule.data.model

data class OfferImageSlider(
    val status: Boolean,
    val data: List<SliderItem>

)

data class SliderItem(
    val id: Int,
    val image: String
)