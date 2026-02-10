package com.example.freshyzoappmodule.ui.Activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.utils.helper.DateHelper
import com.example.freshyzoappmodule.NewMode.view.Activity.ProductListActivity
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivityMainBinding
import com.example.freshyzoappmodule.databinding.BottomSheetDialogBinding
import com.example.freshyzoappmodule.ui.Adapter.DayQuantityAdapter
import com.example.freshyzoappmodule.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
      private val  viewModel: MainViewModel by viewModels()

    private  lateinit var   dateHelper : DateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateHelper = DateHelper(viewModel, this)

        setupObservers()
        setupClicks()
        binding.btnCheck.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }
    }

    private fun setupObservers() {
        // Observe Plan Changes to update UI
        viewModel.currentPlan.observe(this) { plan ->
            when (plan) {
                MainViewModel.PlanType.DAILY -> {
                    binding.weeklyCardView.visibility = View.GONE
                    binding.monthlyCardView.visibility = View.GONE
                    updateTabStyles(isWeekly = false, isDaily = true)
                    updateDateFieldsVisibility(isWeekly = false)
                }
                MainViewModel.PlanType.WEEKLY -> {
                    binding.weeklyCardView.visibility = View.VISIBLE
                    binding.monthlyCardView.visibility = View.GONE
                    updateTabStyles(isWeekly = true)
                    updateDateFieldsVisibility(isWeekly = true)
                }
                MainViewModel.PlanType.MONTHLY -> {
                    binding.weeklyCardView.visibility = View.GONE
                    binding.monthlyCardView.visibility = View.VISIBLE
                    updateTabStyles(isWeekly = false)
                    updateDateFieldsVisibility(isWeekly = false)
                }
            }
        }

        // Observe and display unified price
        viewModel.displayTotalPrice.observe(this) { price ->
            binding.txtTotalPrice.text = "₹$price"
            binding.txtTotalPayablePrice.text = "₹$price"
        }

        // Observe Month and Quantity updates
        viewModel.selectedMonths.observe(this) { months ->
            binding.txtSelectedMonth.text = if (months == 1) "1 Month" else "$months Months"
        }

        viewModel.monthQuantity.observe(this) { qty ->
            binding.txtMonthQuantity.text = "$qty"
        }
        
        // Observe Date changes
        viewModel.weeklyStartDate.observe(this) { it?.let { binding.edtWeeklyStartDate.text = formatDate(it) } }
        viewModel.weeklyEndDate.observe(this) { it?.let { binding.edtWeeklyEndDate.text = formatDate(it) } }
        viewModel.monthlyStartDate.observe(this) { it?.let { binding.edtMonthlyStartDate.text = formatDate(it) } }
        
        // Hide Monthly End Date field as per requirement, but update it for internal state if needed
        viewModel.monthlyEndDate.observe(this) { 
            binding.edtMonthlyEndDate.visibility = View.GONE
        }
    }

    private fun setupClicks() {
        binding.dailyTxt.setOnClickListener { viewModel.setPlan(MainViewModel.PlanType.DAILY) }
        binding.weeklyTxt.setOnClickListener { viewModel.setPlan(MainViewModel.PlanType.WEEKLY) }
        binding.txtMonthly.setOnClickListener { viewModel.setPlan(MainViewModel.PlanType.MONTHLY) }

        binding.btnSelectDaysAndQuantity.setOnClickListener { showWeeklyBottomSheet() }
        binding.btnSelectMonth.setOnClickListener { showMonthDialog() }
        binding.btnMonthQuantity.setOnClickListener { showMonthQuantityDialog() }
        
        binding.edtWeeklyStartDate.setOnClickListener { dateHelper.showDatePicker(isStartDate = true, isMonthly = false) }
        binding.edtWeeklyEndDate.setOnClickListener {
            if (viewModel.weeklyStartDate.value == null) {
                Toast.makeText(this, "Please select Start Date first", Toast.LENGTH_SHORT).show()
            } else {
                dateHelper.showDatePicker(isStartDate = false, isMonthly = false)
            }
        }
        binding.edtMonthlyStartDate.setOnClickListener { dateHelper.showDatePicker(isStartDate = true, isMonthly = true) }

        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, ProductLoadActivity::class.java))
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun formatDate(cal: Calendar): String {
        return "%04d-%02d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun updateDateFieldsVisibility(isWeekly: Boolean) {
        if (isWeekly) {
            binding.edtWeeklyStartDate.visibility = View.VISIBLE
            binding.edtWeeklyEndDate.visibility = View.VISIBLE
            binding.edtMonthlyStartDate.visibility = View.GONE
            binding.edtMonthlyEndDate.visibility = View.GONE
        } else {
            binding.edtWeeklyStartDate.visibility = View.GONE
            binding.edtWeeklyEndDate.visibility = View.GONE
            binding.edtMonthlyStartDate.visibility = View.VISIBLE
            binding.edtMonthlyEndDate.visibility = View.GONE
        }
    }

    private fun updateTabStyles(isWeekly: Boolean, isDaily: Boolean = false) {
        val activeBg = ContextCompat.getDrawable(this, R.drawable.buttonbg)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val primaryColor = ContextCompat.getColor(this, R.color.primary)

        listOf(binding.dailyTxt, binding.weeklyTxt, binding.txtMonthly).forEach {
            it.background = inactiveBg
            it.setTextColor(primaryColor)
        }

        when {
            isDaily -> { binding.dailyTxt.background = activeBg; binding.dailyTxt.setTextColor(whiteColor) }
            isWeekly -> { binding.weeklyTxt.background = activeBg; binding.weeklyTxt.setTextColor(whiteColor) }
            else -> { binding.txtMonthly.background = activeBg; binding.txtMonthly.setTextColor(whiteColor) }
        }
    }

    private fun showWeeklyBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        val adapter = DayQuantityAdapter(mutableListOf()) { index, _ ->
            viewModel.onDayClicked(index)
        }

        sheetBinding.recyclerDates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sheetBinding.recyclerDates.adapter = adapter

        // Setup Observers for this dialog instance
        val dayListObserver = androidx.lifecycle.Observer<List<com.example.freshyzoappmodule.data.model.DayDateModel>> { list ->
            adapter.updateList(list.toMutableList())
            val selectedIndex = viewModel.selectedDayIndex.value ?: -1
            if (selectedIndex != -1) {
                val qty = list[selectedIndex].quantity
                sheetBinding.txtSelectQuantity.text = if (qty > 0) "$qty" else "Select Quantity"
            }
        }

        val selectedDayObserver = androidx.lifecycle.Observer<Int> { index ->
            if (index != -1) {
                val qty = viewModel.dayList.value?.get(index)?.quantity ?: 0
                sheetBinding.txtSelectQuantity.visibility = View.VISIBLE
                sheetBinding.txtSelectQuantity.text = if (qty > 0) "$qty" else "Select Quantity"
            } else {
                sheetBinding.txtSelectQuantity.visibility = View.GONE
            }
        }

        viewModel.dayList.observe(this, dayListObserver)
        viewModel.selectedDayIndex.observe(this, selectedDayObserver)

        // Remove observers when dialog is dismissed
        dialog.setOnDismissListener {
            viewModel.dayList.removeObserver(dayListObserver)
            viewModel.selectedDayIndex.removeObserver(selectedDayObserver)
        }

        sheetBinding.txtSelectQuantity.setOnClickListener {
            val index = viewModel.selectedDayIndex.value ?: -1
            if (index != -1) {
                showWeeklyQuantityDialog { finalQty ->
                    viewModel.updateWeeklyQuantity(finalQty)
                }
            }
        }

        dialog.show()
    }

    private fun showWeeklyQuantityDialog(onSelected: (Int) -> Unit) {
        val options = arrayOf("1", "2", "3", "More")
        AlertDialog.Builder(this)
            .setTitle("Select Quantity")
            .setItems(options) { _, which ->
                if (options[which] == "More") {
                    val input = EditText(this)
                    AlertDialog.Builder(this)
                        .setTitle("Enter Quantity").setView(input)
                        .setPositiveButton("OK") { _, _ ->
                            input.text.toString().toIntOrNull()?.let { if (it > 0) onSelected(it) }
                        }.show()
                } else {
                    onSelected(options[which].toInt())
                }
            }.show()
    }

    private fun showMonthDialog() {
        val options = arrayOf("1 Month", "2 Month", "3 Month", "More")
        AlertDialog.Builder(this).setTitle("Select Month").setItems(options) { _, which ->
            if (options[which] == "More") {
                val input = EditText(this)
                AlertDialog.Builder(this).setTitle("Enter Months").setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        input.text.toString().toIntOrNull()?.let { if (it > 0) viewModel.updateMonthSelection(it) }
                    }.show()
            } else {
                viewModel.updateMonthSelection(which + 1)
            }
        }.show()
    }

    private fun showMonthQuantityDialog() {
        val quantities = arrayOf("1", "2", "3", "4", "More")
        AlertDialog.Builder(this).setTitle("Select Quantity").setItems(quantities) { _, which ->
            if (quantities[which] == "More") {
                val input = EditText(this)
                AlertDialog.Builder(this).setTitle("Enter Quantity").setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        input.text.toString().toIntOrNull()?.let { if (it > 0) viewModel.updateMonthQuantity(it) }
                    }.show()
            } else {
                viewModel.updateMonthQuantity(quantities[which].toInt())
            }
        }.show()
    }


}
