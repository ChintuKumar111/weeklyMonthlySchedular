package com.example.freshyzoappmodule.data.api

import com.example.freshyzoappmodule.data.model.OfferImageSlider
import com.example.freshyzoappmodule.data.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("fetch_product")
    fun getProducts(): Call<List<Product>>

    // showing image slider for active offers
    @GET("slider")
    suspend fun getSliderImages(): Response<OfferImageSlider>
}
