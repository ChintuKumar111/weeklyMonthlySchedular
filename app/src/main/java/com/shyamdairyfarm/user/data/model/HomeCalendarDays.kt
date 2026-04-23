package com.shyamdairyfarm.user.data.model

data class HomeCalendarDays(
    val dayName: String = "",
    val dateNumber: Int = 0,
    val fullDate: String = "",
    var hasDelivery: Boolean = false,
    var isToday: Boolean = false,
    var isSelected: Boolean = false,
    var isMonthHeader: Boolean = false,
    var monthName: String = "",
    var products: List<HomeProductDeliveryCalendar>? = null
)