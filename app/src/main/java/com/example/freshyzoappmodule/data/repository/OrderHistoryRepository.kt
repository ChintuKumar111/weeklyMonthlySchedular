package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderHistoryRepository(private val apiService: ApiService) {

    suspend fun getOrderHistory(): Result<List<OrderHistoryModel>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOrderHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch order history: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
