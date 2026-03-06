package com.example.freshyzoappmodule.data.model

import com.example.freshyzoappmodule.helper.defaultDays
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency

data class ProductSubscribeUiState(
    val selectedFrequency: DeliveryFrequency = DeliveryFrequency.DAILY,
    val simpleQty: Int = 1,
    val dayStates: List<DayState> = defaultDays(),
    val totalPriceText: String = "Subscribe Now ",
    val simpleSummaryText: String = "1 packet × daily",
    val daySummaryText: String = "",
    val isDayMode: Boolean = false,
    val startDate: String = "",
    val shortDate: String = "",
    val deliveryBeginsText: String = "",
    val footerBannerText: String = ""
)
