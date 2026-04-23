package com.shyamdairyfarm.user.data.model

data class TransactionSummary(
    val billMonth: String,
    val customerName: String,
    val mobileNo: String,
    val totalSell: String,
    val totalRecharges: String,
    val availableBalance: String
)
