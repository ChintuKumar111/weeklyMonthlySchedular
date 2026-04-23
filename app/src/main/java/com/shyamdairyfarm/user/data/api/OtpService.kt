package com.shyamdairyfarm.user.data.api

import com.shyamdairyfarm.user.data.model.response.OtpResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface OtpApiService {
    @POST("api/v5/otp/verify")
    suspend fun verifyOtp(
        @Query("authkey") authKey: String,
        @Query("mobile") mobile: String,
        @Query("otp") otp: String
    ): Response<OtpResponse>

    @POST("api/v5/otp")
    suspend fun sendOtp(
        @Query("template_id") templateId: String,
        @Query("mobile") mobile: String,
        @Query("authkey") authKey: String,
        @Query("otp_length") otpLength: Int = 6
    ): Response<OtpResponse>
}