package com.example.freshyzoappmodule.data.model

data class TransactionHeaderRow(
    val srNo: Int,
    val date: String,
    val transaction: String,
    val qty: String,
    val totalSale: String,
    val recharge: String,
    val balance: String
)
