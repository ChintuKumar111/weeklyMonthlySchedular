package com.example.freshyzoappmodule.helper

import androidx.appcompat.app.AppCompatActivity
import com.example.freshyzoappmodule.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CustomDatePickerDialog {
    fun showMaterialDatePicker(
        activity: AppCompatActivity,
        onDateSelected: (formattedDate: String, dayName: String) -> Unit
    ) {

        // Get current local calendar
        val calendar = Calendar.getInstance()

        // 8 AM Cutoff Logic
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 8) {
            calendar.add(Calendar.DATE, 1)
        }

        // Convert selected date to UTC midnight
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
        utcCalendar.set(Calendar.MILLISECOND, 0)

        val minDate = utcCalendar.timeInMillis

        // Apply constraints
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(minDate))
            .build()

        // Build Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Delivery Start Date")
            .setSelection(minDate)   // ✅ Selected when dialog opens
            .setCalendarConstraints(constraints)
            .setTheme(R.style.CustomCalendarTheme)
            .build()

        // Show dialog
        datePicker.show(activity.supportFragmentManager, "DATE_PICKER")

        // Handle date selection
        datePicker.addOnPositiveButtonClickListener { selection ->

            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selection

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedCalendar.time)

            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dayName = dayFormat.format(selectedCalendar.time)

            onDateSelected(formattedDate, dayName)
        }
    }

}