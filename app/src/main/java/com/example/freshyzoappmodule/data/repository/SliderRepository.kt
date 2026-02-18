package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.RetrofitClient

class SliderRepository {

    suspend fun getSliderImages() = RetrofitClient.api.getSliderImages()
}
