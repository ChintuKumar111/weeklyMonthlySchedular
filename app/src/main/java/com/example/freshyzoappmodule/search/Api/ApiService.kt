package com.example.freshyzoappmodule.search.Api

import com.example.freshyzoappmodule.search.model.ProductModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("fetch_product")
    fun getProducts(): Call<List<ProductModel>>
}