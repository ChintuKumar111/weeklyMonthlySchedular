package com.example.freshyzoappmodule.common.helper

import android.R
import android.app.DatePickerDialog
import android.content.Context
import com.example.freshyzoappmodule.viewmodel.MainViewModel
import java.util.Calendar

class DateHelper(private val viewModel: MainViewModel, private val context: Context){



   public fun showDatePicker(isStartDate: Boolean, isMonthly: Boolean) {
       val minDate = viewModel.getMinCalendar()

       val datePickerDialog = DatePickerDialog(
           context,
           R.style.Theme_Material_Light_Dialog_Alert,
           { _, year, month, day ->
               val selectedCal = Calendar.getInstance().apply { set(year, month, day) }
               if (isMonthly) {
                   viewModel.setMonthlyStartDate(selectedCal)
               } else {
                   if (isStartDate) viewModel.setWeeklyStartDate(selectedCal)
                   else viewModel.setWeeklyEndDate(selectedCal)
               }
           },
           minDate.get(Calendar.YEAR),
           minDate.get(Calendar.MONTH),
           minDate.get(Calendar.DAY_OF_MONTH)
       )

       datePickerDialog.datePicker.minDate = minDate.timeInMillis

       if (!isStartDate && !isMonthly) {
           viewModel.weeklyStartDate.value?.let {
               datePickerDialog.datePicker.minDate = it.timeInMillis + (24 * 60 * 60 * 1000)
           }
       }
       datePickerDialog.show()
   }
}