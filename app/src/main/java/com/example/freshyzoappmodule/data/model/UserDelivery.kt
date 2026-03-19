package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDelivery(
    val id: Int = 0,
    val productName: String = "",
    val quantity: String = "",          // e.g. "500 ml · Qty 2"
    val productImageUrl: String = "",   // URL or drawable res name
    val status: String = "Delivered",   // "Delivered", "Pending", "Cancelled"
    val price: Double = 0.0,
    val transactionId: String = "",
    val date: String = "",              // e.g. "26 Feb 2026"
    val remainingBalance: Double = 0.0, // negative = debit
    val remark: String = ""             // empty or "N/A" hides the row
) : Parcelable
