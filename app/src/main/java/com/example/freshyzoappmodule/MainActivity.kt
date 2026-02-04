package com.example.freshyzoappmodule

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.Adapter.DayQuantityAdapter
import com.example.freshyzoappmodule.databinding.ActivityMainBinding
import com.example.freshyzoappmodule.model.DayDateModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var weeklyStartDate: Calendar? = null
    private var monthlyStartDate: Calendar? = null
    private var selectedMonths = 0
    private var monthQuantity = 0
    private var weeklyTotalPrice = 0
    private var monthlyTotalPrice = 0
    private val pricePerUnitPerDay = 60
    private val daysPerMonth = 30

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val dayList = mutableListOf(
        DayDateModel("Mon", 0, false),
        DayDateModel("Tue", 0, false),
        DayDateModel("Wed", 0, false),
        DayDateModel("Thu", 0, false),
        DayDateModel("Fri", 0, false),
        DayDateModel("Sat", 0, false),
        DayDateModel("Sun", 0, false)
    )

    private val monthList = mutableListOf(
        DayDateModel("1", 0, false),
        DayDateModel("2", 0, false),
        DayDateModel("3", 0, false),
        DayDateModel("4", 0, false),
        DayDateModel("5", 0, false),
        DayDateModel("6", 0, false),
        DayDateModel("7", 0, false),
        DayDateModel("8", 0, false),
        DayDateModel("9", 0, false),
        DayDateModel("10", 0, false),
        DayDateModel("11", 0, false),
        DayDateModel("12", 0, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance()
            .subscribeToTopic("all")
            .addOnCompleteListener {
                Log.d("FCM_TOPIC", "Subscribed to ALL users")
            }

        askNotificationPermission()
        setupInsets()
        setupDefaults()
        setupClicks()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupInsets() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupDefaults() {
        binding.txtTotalPrice.text = "0.00"
        binding.txtTotalDiscountPrice.text = "0.00"
        binding.txtDeliveryFee.text = "0.00"
        binding.txtTotalPayablePrice.text = "0.00"
        selectWeeklyTab()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSelectDaysAndQuantity.setOnClickListener { showWeeklyBottomSheet() }
        binding.weeklyTxt.setOnClickListener { selectWeeklyTab() }
        binding.dailyTxt.setOnClickListener { selectDailyTab() }
        binding.txtMonthly.setOnClickListener { selectMonthlyTab() }
        binding.btnSelectMonth.setOnClickListener {
            showMonthDialog()
            binding.edtMonthlyStartDate.text = null
            binding.edtMonthlyEndDate.text = null
        }
        binding.btnMonthQuantity.setOnClickListener { showMonthQuantityDialog() }
        binding.edtWeeklyStartDate.setOnClickListener { showDatePicker(binding.edtWeeklyStartDate, true, isMonthly = false) }
        binding.edtWeeklyEndDate.setOnClickListener { showDatePicker(binding.edtWeeklyEndDate, false, isMonthly = false) }
        binding.edtMonthlyStartDate.setOnClickListener { showDatePicker(binding.edtMonthlyStartDate, true, isMonthly = true) }
        binding.edtMonthlyEndDate.setOnClickListener { showDatePicker(binding.edtMonthlyEndDate, false, isMonthly = true) }
        binding.btnSubscribe.setOnClickListener {
            if (binding.edtWeeklyStartDate.text.toString().isEmpty() &&
                binding.edtWeeklyEndDate.text.toString().isEmpty() &&
                binding.edtMonthlyStartDate.text.toString().isEmpty() &&
                binding.edtMonthlyEndDate.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please select start and end date first", Toast.LENGTH_LONG).show()
            }
        }

        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun selectDailyTab() { highlightTab(binding.dailyTxt) }
    private fun selectWeeklyTab() {
        highlightTab(binding.weeklyTxt)
        binding.weeklyCardView.visibility = View.VISIBLE
        binding.monthlyCardView.visibility = View.GONE
        binding.edtWeeklyStartDate.visibility = View.VISIBLE
        binding.edtWeeklyEndDate.visibility = View.VISIBLE
        binding.edtMonthlyStartDate.visibility = View.GONE
        binding.edtMonthlyEndDate.visibility = View.GONE
        binding.txtTotalPrice.text = "₹$weeklyTotalPrice"
        binding.txtTotalPayablePrice.text = "₹$weeklyTotalPrice"
    }
    private fun selectMonthlyTab() {
        highlightTab(binding.txtMonthly)
        binding.weeklyCardView.visibility = View.GONE
        binding.monthlyCardView.visibility = View.VISIBLE
        binding.edtWeeklyStartDate.visibility = View.GONE
        binding.edtWeeklyEndDate.visibility = View.GONE
        binding.edtMonthlyStartDate.visibility = View.VISIBLE
        binding.edtMonthlyEndDate.visibility = View.VISIBLE
        binding.txtTotalPrice.text = "₹$monthlyTotalPrice"
        binding.txtTotalPayablePrice.text = "₹$monthlyTotalPrice"
        binding.edtMonthlyEndDate.isEnabled = false
    }
    private fun highlightTab(selected: View) {
        listOf(binding.weeklyTxt, binding.dailyTxt, binding.txtMonthly).forEach { it.backgroundTintList = null }
        selected.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_light))
    }

    private fun showWeeklyBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        dialog.setContentView(view)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerDates)
        val btnQuantity = view.findViewById<TextView>(R.id.txtSelectQuantity)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        updateBottomSheetQuantityButton(btnQuantity)
        val adapter = DayQuantityAdapter(dayList, onQuantityChanged = { total ->
            weeklyTotalPrice = total
            binding.txtTotalPrice.text = "₹$weeklyTotalPrice"
            binding.txtTotalPayablePrice.text = "₹$weeklyTotalPrice"
            updateBottomSheetQuantityButton(btnQuantity)
        }, onSelectionChanged = { hasSelection ->
            btnQuantity.visibility = if (hasSelection) View.VISIBLE else View.GONE
            updateBottomSheetQuantityButton(btnQuantity)
        })
        recycler.adapter = adapter
        btnQuantity.setOnClickListener {
            val selectedIndex = dayList.indexOfFirst { it.isSelected }
            if (selectedIndex != -1) showWeeklyQuantityDialog(adapter, selectedIndex, btnQuantity)
            else toast("Please select a day first")
        }
        dialog.show()
    }

    private fun updateBottomSheetQuantityButton(btnQuantity: TextView) {
        val selectedItem = dayList.find { it.isSelected }
        btnQuantity.text = selectedItem?.quantity?.toString() ?: "Select Quantity"
    }

    private fun showWeeklyQuantityDialog(adapter: DayQuantityAdapter, index: Int, btnQuantity: TextView) {
        val quantities = arrayOf("1", "2", "3", "4", "More")
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("Select Quantity")
            .setItems(quantities) { _, which ->
                val selected = quantities[which]
                if (selected == "More") showCustomWeeklyQuantityInput(index, adapter, btnQuantity)
                else updateWeeklyQuantity(selected.toInt(), index, adapter, btnQuantity)
            }.show()
    }

    private fun showCustomWeeklyQuantityInput(index: Int, adapter: DayQuantityAdapter, btnQuantity: TextView) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter Quantity"
        }
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("Enter Quantity").setView(input)
            .setPositiveButton("OK") { _, _ ->
                val qty = input.text.toString().toIntOrNull()
                if (qty != null && qty > 0) updateWeeklyQuantity(qty, index, adapter, btnQuantity)
                else toast("Enter valid quantity")
            }.setNegativeButton("Cancel", null).show()
    }

    private fun updateWeeklyQuantity(qty: Int, index: Int, adapter: DayQuantityAdapter, btnQuantity: TextView) {
        dayList[index].quantity = qty
        dayList[index].isSelected = qty > 0
        adapter.notifyItemChanged(index)
        weeklyTotalPrice = dayList.sumOf { it.quantity } * 60
        binding.txtTotalPrice.text = "₹$weeklyTotalPrice"
        binding.txtTotalPayablePrice.text = "₹$weeklyTotalPrice"
        updateBottomSheetQuantityButton(btnQuantity)
    }

    private fun showMonthDialog() {
        val months = monthList.map { it.day }.toTypedArray()
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("Select Month")
            .setItems(months) { _, which ->
                selectedMonths = months[which].toInt()
                binding.txtSelectedMonth.text = "${months[which]} Month"
                monthList.forEach { it.isSelected = false }
                monthList[which].isSelected = true
                calculateMonthlyTotal()
            }.show()
    }

    private fun showMonthQuantityDialog() {
        val quantities = arrayOf("1", "2", "3", "4", "More")
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("Select Quantity")
            .setItems(quantities) { _, which ->
                val selected = quantities[which]
                if (selected == "More") showCustomMonthQuantityInput()
                else {
                    monthQuantity = selected.toInt()
                    binding.txtMonthQuantity.text = "Qty: $selected"
                    calculateMonthlyTotal()
                }
            }.show()
    }

    private fun showCustomMonthQuantityInput() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter Quantity"
        }
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("Enter Quantity").setView(input)
            .setPositiveButton("OK") { _, _ ->
                val qty = input.text.toString().toIntOrNull()
                if (qty != null && qty > 0) {
                    monthQuantity = qty
                    binding.txtMonthQuantity.text = "Qty: $qty"
                    calculateMonthlyTotal()
                } else toast("Enter valid quantity")
            }.setNegativeButton("Cancel", null).show()
    }

    private fun calculateMonthlyTotal() {
        if (selectedMonths == 0 || monthQuantity == 0) return
        monthlyTotalPrice = selectedMonths * daysPerMonth * monthQuantity * pricePerUnitPerDay
        binding.txtTotalPrice.text = "₹$monthlyTotalPrice"
        binding.txtTotalPayablePrice.text = "₹$monthlyTotalPrice"
    }

    private fun getDayOfWeekInt(day: String): Int = when (day) {
        "Sun" -> Calendar.SUNDAY; "Mon" -> Calendar.MONDAY; "Tue" -> Calendar.TUESDAY
        "Wed" -> Calendar.WEDNESDAY; "Thu" -> Calendar.THURSDAY; "Fri" -> Calendar.FRIDAY
        "Sat" -> Calendar.SATURDAY; else -> -1
    }

    private fun showDatePicker(textView: TextView, isStartDate: Boolean, isMonthly: Boolean) {
        val cal = Calendar.getInstance()
        if (isStartDate && cal.get(Calendar.HOUR_OF_DAY) >= 9) cal.add(Calendar.DAY_OF_MONTH, 1)
        if (!isMonthly && !isStartDate && weeklyStartDate != null) cal.timeInMillis = weeklyStartDate!!.timeInMillis + (24 * 60 * 60 * 1000)

        DatePickerDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert, { _, year, month, dayOfMonth ->
            val selectedCal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            if (!isMonthly && isStartDate) {
                val allowedDays = dayList.filter { it.quantity > 0 }.map { getDayOfWeekInt(it.day) }
                if (allowedDays.isNotEmpty() && selectedCal.get(Calendar.DAY_OF_WEEK) !in allowedDays) {
                    while (selectedCal.get(Calendar.DAY_OF_WEEK) !in allowedDays) selectedCal.add(Calendar.DAY_OF_MONTH, 1)
                    toast("Adjusted to nearest delivery day")
                }
            }
            textView.text = "%04d-%02d-%02d".format(selectedCal.get(Calendar.YEAR), selectedCal.get(Calendar.MONTH) + 1, selectedCal.get(Calendar.DAY_OF_MONTH))
            if (isMonthly) {
                if (isStartDate) {
                    monthlyStartDate = selectedCal
                    if (selectedMonths > 0) {
                        val endCal = selectedCal.clone() as Calendar
                        endCal.add(Calendar.DAY_OF_MONTH, selectedMonths * daysPerMonth)
                        binding.edtMonthlyEndDate.text = "%04d-%02d-%02d".format(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH) + 1, endCal.get(Calendar.DAY_OF_MONTH))
                    }
                }
            } else if (isStartDate) {
                weeklyStartDate = selectedCal
                binding.edtWeeklyEndDate.text = ""
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply { datePicker.minDate = cal.timeInMillis }.show()
    }

    private fun toast(msg: String) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}
