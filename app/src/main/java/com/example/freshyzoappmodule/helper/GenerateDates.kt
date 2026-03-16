package com.example.freshyzoappmodule.helper

import com.example.freshyzoappmodule.data.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun generateDates(): List<CalendarDay> {

    val calendar = Calendar.getInstance()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(calendar.time)

    val list = mutableListOf<CalendarDay>()
    var currentMonth = -1

    for (i in 0 until 730) {
        val month = calendar.get(Calendar.MONTH)
        var isHeader = false
        var monthName = ""

        if (month != currentMonth) {
            currentMonth = month
            isHeader = true
            monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        }

        val fullDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(calendar.time)

        val dayName = SimpleDateFormat("EEE", Locale.getDefault())
            .format(calendar.time)

        val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)

        list.add(
            CalendarDay(
                dayName = dayName,
                dateNumber = dateNumber,
                fullDate = fullDate,
                isToday = fullDate == today,
                isMonthHeader = isHeader,
                monthName = monthName
            )
        )

        calendar.add(Calendar.DATE, 1)
    }

    return list
}