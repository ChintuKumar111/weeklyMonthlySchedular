package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.Delivery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeliveryRepository(private val apiService: ApiService) {
    suspend fun getDeliveries(): Result<List<Delivery>> = withContext(Dispatchers.IO) {
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
