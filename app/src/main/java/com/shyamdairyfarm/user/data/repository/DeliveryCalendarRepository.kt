package com.shyamdairyfarm.user.data.repository

import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.model.response.CalendarResponse
import com.shyamdairyfarm.user.data.model.response.DeliveryDetailsCalendarResponse

class DeliveryCalendarRepository(private val api: ApiService) {

    suspend fun getCalendar(month: Int, year: Int): CalendarResponse {
        return api.getCalendar(month, year)
    }

    suspend fun getCalendarDeliveryDetails(date: String): DeliveryDetailsCalendarResponse {
        return api.getCalendarDeliveryDetails(date)
    }
}