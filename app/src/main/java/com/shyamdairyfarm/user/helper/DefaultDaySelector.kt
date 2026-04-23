package com.shyamdairyfarm.user.helper

import com.shyamdairyfarm.user.data.model.WeeklyDayState

fun defaultDays(): List<WeeklyDayState> {
    return listOf(
        WeeklyDayState("Mon", true, 1),
        WeeklyDayState("Tue", false, 0),
        WeeklyDayState("Wed", false, 0),
        WeeklyDayState("Thu", false, 0),
        WeeklyDayState("Fri", false, 0),
        WeeklyDayState("Sat", true, 2),
        WeeklyDayState("Sun", false, 0),
    )
}