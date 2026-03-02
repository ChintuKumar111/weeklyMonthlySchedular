package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// In AddressRepository.kt
class AddressRepository(private val apiService: ApiService) {

    suspend fun updateAddress(address: Address): Result<Address> = withContext(Dispatchers.IO) {
        try {
            //make changes for actual api response
            val response = apiService.updateAddress(address)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update address"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSavedAddress(): Result<Address> =
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
