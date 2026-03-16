package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService

class SliderRepository(private val api: ApiService) {

    suspend fun getSliderImages() = api.getSliderImages()

    suspend fun getComboOffers() = api.getComboOffers()

    suspend fun getBlogReports() = api.getBlogReports()
}
