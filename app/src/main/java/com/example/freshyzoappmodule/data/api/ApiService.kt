package com.example.freshyzoappmodule.data.api

import com.example.freshyzoappmodule.data.model.Address
import com.example.freshyzoappmodule.data.model.BlogReport
import com.example.freshyzoappmodule.data.model.ComboOffer
import com.example.freshyzoappmodule.data.model.Delivery
import com.example.freshyzoappmodule.data.model.BannerResponse
import com.example.freshyzoappmodule.data.model.CalendarDay
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.SubscriptionResponse
import com.example.freshyzoappmodule.data.model.response.CalendarResponse
import com.example.freshyzoappmodule.data.model.response.DeliveryDetailsCalendarResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("fetch_product")
    fun getProducts(): Call<List<Product>>

    // showing image slider for active offers
    @GET("slider")
    suspend fun getSliderImages(): Response<BannerResponse>

    @GET("combo_offers")
    suspend fun getComboOffers(): Response<List<ComboOffer>>

    @GET("blog_reports")
    suspend fun getBlogReports(): Response<List<BlogReport>>

    // for set the address of user and save to server and also show the updated address===========

    @GET("get_address") // Replace with your actual endpoint
    suspend fun getSavedAddress(): Response<Address>

    @POST("update_address") // Replace with your actual endpoint
    suspend fun updateAddress(@Body address: Address): Response<Address>

    @GET("fetch_deliveries") // Replace with actual endpoint if known
    suspend fun getDeliveries(): Response<List<Delivery>>

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