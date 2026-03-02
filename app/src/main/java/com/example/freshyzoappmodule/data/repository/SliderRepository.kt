package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.RetrofitClient

class SliderRepository {

    suspend fun getSliderImages() = RetrofitClient.api.getSliderImages()

    suspend fun getComboOffers() = RetrofitClient.api.getComboOffers()

    suspend fun getBlogReports() = RetrofitClient.api.getBlogReports()
}
