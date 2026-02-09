package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData
import com.example.freshyzoappmodule.data.model.DayDateModel
import java.util.Calendar

class MainViewModel : ViewModel() {

    val pricePerUnitPerDay = 60
    val daysPerMonth = 30

    private val _selectedMonths = MutableLiveData(1)
    val selectedMonths: LiveData<Int> = _selectedMonths

    private val _monthQuantity = MutableLiveData(1)
    val monthQuantity: LiveData<Int> = _monthQuantity

    private val _currentPlan = MutableLiveData(PlanType.WEEKLY)
    val currentPlan: LiveData<PlanType> = _currentPlan

    private val _weeklyStartDate = MutableLiveData<Calendar?>()
    val weeklyStartDate: LiveData<Calendar?> = _weeklyStartDate

    private val _weeklyEndDate = MutableLiveData<Calendar?>()
    val weeklyEndDate: LiveData<Calendar?> = _weeklyEndDate

    private val _monthlyStartDate = MutableLiveData<Calendar?>()
    val monthlyStartDate: LiveData<Calendar?> = _monthlyStartDate

    // Derived Monthly End Date
    val monthlyEndDate = MediatorLiveData<Calendar?>().apply {
        addSource(_monthlyStartDate) { value = calculateEndDate() }
        addSource(_selectedMonths) { value = calculateEndDate() }
    }

    private val _weeklyTotalPrice = MutableLiveData(0)
    val weeklyTotalPrice: LiveData<Int> = _weeklyTotalPrice

    private val _monthlyTotalPrice = MutableLiveData(0)
    val monthlyTotalPrice: LiveData<Int> = _monthlyTotalPrice

    // Unified Price Observer
    val displayTotalPrice = MediatorLiveData<Int>().apply {
        addSource(_currentPlan) { plan ->
            value = if (plan == PlanType.WEEKLY) _weeklyTotalPrice.value else _monthlyTotalPrice.value
        }
        addSource(_weeklyTotalPrice) { price ->
            if (_currentPlan.value == PlanType.WEEKLY) value = price
        }
        addSource(_monthlyTotalPrice) { price ->
            if (_currentPlan.value == PlanType.MONTHLY) value = price
        }
    }

    // Selected Day in Bottom Sheet
    private val _selectedDayIndex = MutableLiveData(-1)
    val selectedDayIndex: LiveData<Int> = _selectedDayIndex

    enum class PlanType { DAILY, WEEKLY, MONTHLY }

    private val _dayList = MutableLiveData<List<DayDateModel>>(
        listOf(
            DayDateModel("Mon", 0, false),
            DayDateModel("Tue", 0, false),
            DayDateModel("Wed", 0, false),
            DayDateModel("Thu", 0, false),
            DayDateModel("Fri", 0, false),
            DayDateModel("Sat", 0, false),
            DayDateModel("Sun", 0, false)
        )
    )
    val dayList: LiveData<List<DayDateModel>> = _dayList

    init {
        calculateMonthlyTotal()
    }

    fun setPlan(plan: PlanType) {
        _currentPlan.value = plan
    }

    fun onDayClicked(index: Int) {
        val list = _dayList.value?.toMutableList() ?: return
        val currentDay = list[index]
        
        if (currentDay.quantity > 0) {
            currentDay.quantity = 0
            currentDay.isSelected = false
            _selectedDayIndex.value = -1
        } else {
            currentDay.quantity = 1
            currentDay.isSelected = true
            _selectedDayIndex.value = index
        }
        
        _dayList.value = list
        updateWeeklyTotalPrice()
    }

    fun updateWeeklyQuantity(qty: Int) {
        val index = _selectedDayIndex.value ?: return
        if (index == -1) return
        
        val list = _dayList.value?.toMutableList() ?: return
        list[index].quantity = qty
        list[index].isSelected = qty > 0
        
        _dayList.value = list
        updateWeeklyTotalPrice()
    }

    private fun updateWeeklyTotalPrice() {
        val total = _dayList.value?.sumOf { it.quantity } ?: 0
        _weeklyTotalPrice.value = total * pricePerUnitPerDay
    }

    fun updateMonthSelection(months: Int) {
        _selectedMonths.value = months
        calculateMonthlyTotal()
    }

    fun updateMonthQuantity(qty: Int) {
        _monthQuantity.value = qty
        calculateMonthlyTotal()
    }

    private fun calculateMonthlyTotal() {
        _monthlyTotalPrice.value =
            (_selectedMonths.value ?: 1) * daysPerMonth * (_monthQuantity.value ?: 1) * pricePerUnitPerDay
    }

    fun setWeeklyStartDate(cal: Calendar) { _weeklyStartDate.value = cal }
    fun setWeeklyEndDate(cal: Calendar) { _weeklyEndDate.value = cal }
    fun setMonthlyStartDate(cal: Calendar) { _monthlyStartDate.value = cal }

    fun getMinCalendar(): Calendar {
        return Calendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) >= 9) add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun calculateEndDate(): Calendar? {
        val start = _monthlyStartDate.value ?: return null
        return (start.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, (_selectedMonths.value ?: 1) * daysPerMonth)
        }
    }
}
