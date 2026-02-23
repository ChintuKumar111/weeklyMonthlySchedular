package com.example.freshyzoappmodule.ui.activity.comparison

data class ComparisonItem(
    val feature: String,
    val amul: ComparisonStatus,
    val motherDairy: ComparisonStatus,
    val nestle: ComparisonStatus
)

enum class ComparisonStatus {
    YES, NO, PARTIAL
}
