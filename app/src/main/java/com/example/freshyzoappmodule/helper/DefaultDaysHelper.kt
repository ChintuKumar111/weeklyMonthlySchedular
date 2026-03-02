package com.example.freshyzoappmodule.helper

import com.example.freshyzoappmodule.data.model.DayState

fun defaultDays(): List<DayState> {
    return listOf(
        DayState("Mon", true, 1),
        DayState("Tue", false, 0),
        DayState("Wed", false, 0),
        DayState("Thu", false, 0),
        DayState("Fri", false, 0),
        DayState("Sat", true, 2),
        DayState("Sun", false, 0),
    )
}