package com.shyamdairyfarm.user.data.api

import com.shyamdairyfarm.user.data.model.UserAddress
import com.shyamdairyfarm.user.data.model.HomeBlogs
import com.shyamdairyfarm.user.data.model.HomeComboOffers
import com.shyamdairyfarm.user.data.model.UserDelivery
import com.shyamdairyfarm.user.data.model.response.BannerResponse
import com.shyamdairyfarm.user.data.model.OrderHistoryModel
import com.shyamdairyfarm.user.data.model.ProductDetails
import com.shyamdairyfarm.user.data.model.auth.req.GetOtpReq
import com.shyamdairyfarm.user.data.model.auth.req.VerifyOtpReq
import com.shyamdairyfarm.user.data.model.auth.res.OtpRes
import com.shyamdairyfarm.user.data.model.response.SubscriptionResponse
import com.shyamdairyfarm.user.data.model.response.CalendarResponse
import com.shyamdairyfarm.user.data.model.response.DeliveryDetailsCalendarResponse
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


    /////////////////////////////////////////////////////////

    @POST("auth/send_otp") // Replace with your actual endpoint
    suspend fun requestOtp(@Body body: GetOtpReq): Response<OtpRes>


    @POST("auth/verify_otp") // Replace with your actual endpoint
    suspend fun verifyOtp(@Body body: VerifyOtpReq): Response<OtpRes>
}