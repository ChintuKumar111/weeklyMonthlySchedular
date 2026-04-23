package com.shyamdairyfarm.user.data.repository

import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.model.UserAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// In AddressRepository.kt
class AddressRepository(private val apiService: ApiService) {

    suspend fun updateAddress(userAddress: UserAddress): Result<UserAddress> = withContext(Dispatchers.IO) {
        try {
            //make changes for actual api response
            val response = apiService.updateAddress(userAddress)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update address"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSavedAddress(): Result<UserAddress> =
        withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSavedAddress()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch address"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
