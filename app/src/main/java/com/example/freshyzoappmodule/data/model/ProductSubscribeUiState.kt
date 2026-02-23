package com.example.freshyzoappmodule.data.model

import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency

data class ProductSubscribeUiState(
    val simpleQty: Int = 1,
    val selectedFrequency: DeliveryFrequency = DeliveryFrequency.DAILY,
    val totalPriceText: String = "Subscribe Now · ₹70",
    val simpleSummaryText: String = "1 packet × daily",
    val daySummaryText: String = "",
    val isDayMode: Boolean = false
)