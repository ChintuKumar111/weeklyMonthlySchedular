package com.shyamdairyfarm.user.data.repository

import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.model.response.SubscriptionResponse


class SubscriptionRepository(private val api: ApiService) {
    suspend fun getSubscriptions(): List<SubscriptionResponse>? {

        val response = api.getSubscriptions()

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun pauseSubscription(item: SubscriptionResponse) {

        // API call here
    }

    suspend fun cancelSubscription(item: SubscriptionResponse) {

        // API call here
    }
}