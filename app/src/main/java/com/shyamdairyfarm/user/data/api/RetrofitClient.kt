package com.shyamdairyfarm.user.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://freshyzo.com/admin/Customer_App_Api/"
    private const val OTP_BASE_URL = "https://control.msg91.com/"

    // For your main app APIs
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // For Msg91 OTP APIs - Note the type change to OtpApiService
    val otpApi: OtpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OTP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OtpApiService::class.java)
    }
}