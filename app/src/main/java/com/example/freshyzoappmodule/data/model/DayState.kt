package com.example.freshyzoappmodule.data.model

// ── Data class for each day's state ───────────────────────
data class DayState(
    val name: String,
    var isOn: Boolean,
    var qty: Int
)