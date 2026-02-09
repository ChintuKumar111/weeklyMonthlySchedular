package com.example.freshyzoappmodule.data.api

import com.example.freshyzoappmodule.data.model.ProductModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("fetch_product")
    fun getProducts(): Call<List<ProductModel>>
}