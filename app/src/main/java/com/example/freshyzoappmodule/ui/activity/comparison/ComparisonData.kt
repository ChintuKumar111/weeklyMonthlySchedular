package com.example.freshyzoappmodule.ui.activity.comparison

object ComparisonData {

    fun getMilkComparison(): List<ComparisonItem> {
        return listOf(
            ComparisonItem("A2 Milk Available", ComparisonStatus.PARTIAL, ComparisonStatus.NO, ComparisonStatus.NO),
            ComparisonItem("Preservatives Free", ComparisonStatus.PARTIAL, ComparisonStatus.PARTIAL, ComparisonStatus.NO),
            ComparisonItem("Bilona Ghee", ComparisonStatus.NO, ComparisonStatus.NO, ComparisonStatus.NO),
            ComparisonItem("No Starch in Paneer", ComparisonStatus.YES, ComparisonStatus.PARTIAL, ComparisonStatus.NO),
            ComparisonItem("Farm Direct", ComparisonStatus.PARTIAL, ComparisonStatus.PARTIAL, ComparisonStatus.NO),
            ComparisonItem("Delivered < 8 hrs", ComparisonStatus.NO, ComparisonStatus.NO, ComparisonStatus.NO),
            ComparisonItem("Glass Bottle", ComparisonStatus.NO, ComparisonStatus.NO, ComparisonStatus.NO),
            ComparisonItem("Lab Tested Daily", ComparisonStatus.PARTIAL, ComparisonStatus.PARTIAL, ComparisonStatus.PARTIAL)
        )
    }
}