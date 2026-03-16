package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.response.CalendarResponse
import com.example.freshyzoappmodule.data.model.response.DeliveryDetailsCalendarResponse

class DeliveryCalendarRepository(private val api: ApiService) {

    suspend fun getCalendar(month: Int, year: Int): CalendarResponse {
        return api.getCalendar(month, year)
    }

    suspend fun getCalendarDeliveryDetails(date: String): DeliveryDetailsCalendarResponse {
        return api.getCalendarDeliveryDetails(date)
    }
}