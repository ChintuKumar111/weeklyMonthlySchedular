package com.example.freshyzoappmodule.data.api

import com.example.freshyzoappmodule.data.model.Address
import com.example.freshyzoappmodule.data.model.BlogReport
import com.example.freshyzoappmodule.data.model.ComboOffer
import com.example.freshyzoappmodule.data.model.OfferImageSlider
import com.example.freshyzoappmodule.data.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("fetch_product")
    fun getProducts(): Call<List<Product>>

    // showing image slider for active offers
    @GET("slider")
    suspend fun getSliderImages(): Response<OfferImageSlider>

    @GET("combo_offers")
    suspend fun getComboOffers(): Response<List<ComboOffer>>

    @GET("blog_reports")
    suspend fun getBlogReports(): Response<List<BlogReport>>

    // for set the address of user and save to server and also show the updated address===========

    @GET("get_address") // Replace with your actual endpoint
    suspend fun getSavedAddress(): Response<Address>

    @POST("update_address") // Replace with your actual endpoint
    suspend fun updateAddress(@Body address: Address): Response<Address>
}
