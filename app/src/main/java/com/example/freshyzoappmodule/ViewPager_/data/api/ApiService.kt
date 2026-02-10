package com.example.freshyzoappmodule.ViewPager_.data.api


import com.example.freshyzoappmodule.ViewPager_.data.model.ProductListResponse
import com.example.freshyzoappmodule.ViewPager_.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ProductResponse
}
