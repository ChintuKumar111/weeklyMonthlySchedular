package com.shyamdairyfarm.user.data.model

// ── Data class for each day's state ───────────────────────
data class WeeklyDayState(
    val name: String,
    val isOn: Boolean,
    val qty: Int
)