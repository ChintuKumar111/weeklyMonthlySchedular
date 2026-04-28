package com.shyamdairyfarm.user.data.repository

import android.util.Log
import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.model.auth.req.GetOtpReq
import com.shyamdairyfarm.user.data.model.auth.req.RegisterNewCustomerReq
import com.shyamdairyfarm.user.data.model.auth.req.VerifyOtpReq
import com.shyamdairyfarm.user.data.model.auth.res.CustomerLoginRes
import com.shyamdairyfarm.user.data.model.auth.res.OtpRes
import com.shyamdairyfarm.user.data.model.auth.res.RegisterNewCustomerRes
import com.shyamdairyfarm.user.data.utils.getDeviceInfo
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepository(private val apiService: ApiService) {

    fun requestOtp(
        phone: String,
        deviceModel: String = getDeviceInfo()
    ): Flow<UiState<OtpRes>> = flow {

        emit(UiState.Loading)

        val response = apiService.requestOtp(
            GetOtpReq(
                mobile_no = phone,
                device_model = deviceModel
            )
        )

        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.status) {
                emit(UiState.Success(body))
            } else {
                emit(UiState.Error(body.message))
            }
        } else {
            emit(UiState.Error(response.message()))
        }

    }.catch { e ->

        val errorMessage = when (e) {
            is java.net.UnknownHostException -> "No internet connection"
            is java.net.SocketTimeoutException -> "Connection timed out"
            is java.io.IOException -> "Please check your internet connection"
            else -> e.localizedMessage ?: "Something went wrong"
        }

        emit(UiState.Error(errorMessage, e))
    }

    fun verifyOtp(
        phone: String,
        otp: String
    ): Flow<UiState<CustomerLoginRes>> = flow {

        emit(UiState.Loading)

        val response = apiService.verifyOtp(
            VerifyOtpReq(mobile_no = phone, otp = otp)
        )

        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.status) {
                emit(UiState.Success(body))
            } else {
                emit(UiState.Error(body.message))
            }
        } else {
            emit(UiState.Error(response.message()))
        }

    }.catch { e ->

        val errorMessage = when (e) {
            is java.net.UnknownHostException -> "No internet connection"
            is java.net.SocketTimeoutException -> "Connection timed out"
            is java.io.IOException -> "Please check your internet connection"
            else -> e.localizedMessage ?: "Something went wrong"
        }

        emit(UiState.Error(errorMessage, e))
    }


    fun registerNewCustomer(
        firstName: String,
        lastName: String,
        lat: String,
        lng: String,
        address: String
    ): Flow<UiState<RegisterNewCustomerRes>> = flow {

        emit(UiState.Loading)
        val requestBody = RegisterNewCustomerReq(
            first_name = firstName,
            last_name = lastName,
            lat = lat,
            lng = lng,
            address = address
        )

        println("request body $requestBody")
        val response = apiService.registerNewCustomer(
            body = requestBody
        )



        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (body.status) {
//                token expire -> true
                if (body.message.contains("expire", true)) {
                    emit(UiState.ExpiredToken(body))

                } else {

                    emit(UiState.Success(body))
                }
            } else {

                if (body.message.contains("unauthorized", true)) {
                    emit(UiState.UnauthorizedAccess(body.message ?: "Unauthorized Access"))
                } else {
                    emit(UiState.Error(body.message))
                }
            }
        } else {
            emit(UiState.Error(response.message()))
        }

    }
        .catch { e ->


            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timed out"
                is java.io.IOException -> "Please check your internet connection"
                else -> e.localizedMessage ?: "Something went wrong"
            }

            emit(UiState.Error(errorMessage, e))
        }
}