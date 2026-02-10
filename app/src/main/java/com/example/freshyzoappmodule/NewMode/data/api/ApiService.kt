package com.example.freshyzoappmodule.NewMode.data.api

import com.example.freshyzoappmodule.NewMode.data.model.ProductListResponse
import com.example.freshyzoappmodule.NewMode.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ProductResponse
}
