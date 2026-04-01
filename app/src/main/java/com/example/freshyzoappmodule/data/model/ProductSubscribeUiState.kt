package com.example.freshyzoappmodule.data.model

import com.example.freshyzoappmodule.helper.defaultDays
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency

data class ProductSubscribeUiState(
    val selectedFrequency: DeliveryFrequency = DeliveryFrequency.DAILY,
    val simpleQty: Int = 1,
    val weeklyDayStates: List<WeeklyDayState> = defaultDays(),
    val totalPriceText: String = "Subscribe Now ",
    val simpleSummaryText: String = "1 packet × daily",
    val daySummaryText: String = "",
    val isDayMode: Boolean = false,
    val startDate: String = "",
    val shortDate: String = "",
    val deliveryBeginsText: String = "",
    val footerBannerText: String = "",
    // Price Summary Fields
    val basePrice: Int = 0,
    val mrpPrice: Int = 0,
    val packetsPerDelivery: Int = 0,
    val deliveriesPerMonth: Int = 0,
    val subtotalMrp: Double = 0.0,
    val productDiscount: Double = 0.0,
    val subscriptionDiscount: Double = 0.0,
    val totalMonthly: Double = 0.0,
    val perDeliveryAvg: Double = 0.0,
    val perPacketAvg: Double = 0.0,
    val selectedInterval: Int = 1,
)
