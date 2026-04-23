package com.shyamdairyfarm.user.data.repository

import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.model.UserDelivery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeliveryRepository(private val apiService: ApiService) {
    suspend fun getDeliveries(): Result<List<UserDelivery>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDeliveries()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch deliveries: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
