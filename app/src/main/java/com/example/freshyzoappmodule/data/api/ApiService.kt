package com.example.freshyzoappmodule.data.api

import com.example.freshyzoappmodule.data.model.UserAddress
import com.example.freshyzoappmodule.data.model.HomeBlogs
import com.example.freshyzoappmodule.data.model.HomeComboOffers
import com.example.freshyzoappmodule.data.model.UserDelivery
import com.example.freshyzoappmodule.data.model.response.BannerResponse
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import com.example.freshyzoappmodule.data.model.ProductDetails
import com.example.freshyzoappmodule.data.model.response.SubscriptionResponse
import com.example.freshyzoappmodule.data.model.response.CalendarResponse
import com.example.freshyzoappmodule.data.model.response.DeliveryDetailsCalendarResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface   ApiService {
    @GET("fetch_product")
    fun getProducts(): Call<List<ProductDetails>>

    // showing image slider for active offers
    @GET("slider")
    suspend fun getSliderImages(): Response<BannerResponse>

    @GET("combo_offers")
    suspend fun getComboOffers(): Response<List<HomeComboOffers>>

    @GET("blog_reports")
    suspend fun getBlogReports(): Response<List<HomeBlogs>>

    // for set the address of user and save to server and also show the updated address===========

    @GET("get_address") // Replace with your actual endpoint
    suspend fun getSavedAddress(): Response<UserAddress>

    @POST("update_address") // Replace with your actual endpoint
    suspend fun updateAddress(@Body userAddress: UserAddress): Response<UserAddress>

    @GET("fetch_deliveries") // Replace with actual endpoint if known
    suspend fun getDeliveries(): Response<List<UserDelivery>>

    @GET("fetch_order_history") // Replace with your actual endpoint
    suspend fun getOrderHistory(): Response<List<OrderHistoryModel>>

    @GET("subscriptions")
    suspend fun getSubscriptions(): Response<List<SubscriptionResponse>>

    @GET("delivery/calendar")
    suspend fun getCalendar(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): CalendarResponse


    @GET("delivery/details")
    suspend fun getCalendarDeliveryDetails(
        @Query("date") date: String
    ): DeliveryDetailsCalendarResponse
}