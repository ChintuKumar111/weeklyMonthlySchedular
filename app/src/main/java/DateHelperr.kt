import androidx.appcompat.app.AppCompatActivity
import com.example.freshyzoappmodule.R
import com.google.android.material.datepicker.*
import java.text.SimpleDateFormat
import java.util.*

class DateHelperr {

    fun showMaterialDatePicker(
        activity: AppCompatActivity,
        onDateSelected: (formattedDate: String, dayName: String) -> Unit
    ) {

        val calendar = Calendar.getInstance()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        // ✅ 8 AM Cut-off Logic
        if (currentHour >= 8) {
            calendar.add(Calendar.DATE, 1)
        }

        // Reset time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val minDate = calendar.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(minDate))
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Delivery Start Date")
            .setSelection(minDate) // 🔥 Auto Select Here
            .setCalendarConstraints(constraints)
            .setTheme(R.style.CustomCalendarTheme) // 🔥 Apply primary theme
            .build()

        datePicker.show(activity.supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            selectedCalendar.timeInMillis = selection

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedCalendar.time)

            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dayName = dayFormat.format(selectedCalendar.time)

            onDateSelected(formattedDate, dayName)
        }
    }
}