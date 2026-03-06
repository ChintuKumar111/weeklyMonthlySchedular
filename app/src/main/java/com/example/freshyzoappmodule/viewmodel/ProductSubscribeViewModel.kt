package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.DayState
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProductSubscribeViewModel : ViewModel() {
    private var basePrice: Int = 0

    private val _uiState = MutableLiveData(ProductSubscribeUiState())
    val uiState: LiveData<ProductSubscribeUiState> = _uiState

    init {
        val calendar = Calendar.getInstance()
        // ✅ 8 AM Cut-off Logic
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 8) {
            calendar.add(Calendar.DATE, 1)
        }
        updateDate(calendar.time)
    }

    fun updateDate(date: Date) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val shortDateFormat = SimpleDateFormat("MMM\ndd", Locale.getDefault())
        val state = _uiState.value ?: ProductSubscribeUiState()
        updateState(state.copy(
            startDate = dateFormat.format(date),
            shortDate = shortDateFormat.format(date),
            deliveryBeginsText = "Delivery begins ${dayFormat.format(date)}"
        ))
    }

    fun updateDateSelection(formattedDate: String, dayName: String) {
        // Since formattedDate comes from a helper, we might need to parse it back or just update labels
        val state = _uiState.value ?: ProductSubscribeUiState()

        // Extracting Day/Month from formattedDate (assuming "dd MMMM yyyy")
        var displayShort = "📅"
        try {
            val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(formattedDate)
            if (date != null) {
                displayShort = SimpleDateFormat("MMM\ndd", Locale.getDefault()).format(date)
            }
        } catch (e: Exception) { e.printStackTrace() }

        updateState(state.copy(
            startDate = formattedDate,
            shortDate = displayShort,
            deliveryBeginsText = "Delivery begins $dayName"
        ))
    }

    fun setQuantity(quantity: Int) {
        val state = _uiState.value ?: return
        updateState(state.copy(simpleQty = quantity))
    }

    fun selectFrequency(freq: DeliveryFrequency) {
        val current = _uiState.value ?: return

        val newDays = when (freq) {
            // 🔥 WEEKLY → Select all days
            DeliveryFrequency.WEEKLY -> {
                current.dayStates.map { day ->
                    day.copy(isOn = true, qty = if (day.qty == 0) 1 else day.qty)
                }
            }
            // 🔥 ALTERNATE → Select Mon Wed Fri Sun (index 0, 2, 4, 6)
            DeliveryFrequency.ALTERNATE -> {
                current.dayStates.mapIndexed { index, day ->
                    if (index % 2 == 0) {
                        day.copy(isOn = true, qty = if (day.qty == 0) 1 else day.qty)
                    } else {
                        day.copy(isOn = false, qty = 0)
                    }
                }
            }
            // DAILY & MONTHLY → Don't touch days
            else -> current.dayStates
        }

        updateState(
            current.copy(
                selectedFrequency = freq,
                dayStates = newDays
            )
        )
    }

    fun increaseSimpleQty() {
        val state = _uiState.value ?: return
        if (state.simpleQty < 10) {
            updateState(state.copy(simpleQty = state.simpleQty + 1))
        }
    }

    fun decreaseSimpleQty() {
        val state = _uiState.value ?: return
        if (state.simpleQty > 1) {
            updateState(state.copy(simpleQty = state.simpleQty - 1))
        }
    }

    fun setBasePrice(price: Int) {
        basePrice = price
        updateState(_uiState.value ?: ProductSubscribeUiState())
    }

    fun toggleDay(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.dayStates.mapIndexed { i, day ->
            if (i == index) {
                if (day.isOn)
                    day.copy(isOn = false, qty = 0)
                else
                    day.copy(isOn = true, qty = 1)
            } else day
        }
        updateState(state.copy(dayStates = newDays))
    }

    fun increaseDayQty(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.dayStates.mapIndexed { i, day ->
            if (i == index && day.isOn && day.qty < 10)
                day.copy(qty = day.qty + 1)
            else day
        }
        updateState(state.copy(dayStates = newDays))
    }

    fun decreaseDayQty(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.dayStates.mapIndexed { i, day ->
            if (i == index && day.isOn && day.qty > 1)
                day.copy(qty = day.qty - 1)
            else day
        }
        updateState(state.copy(dayStates = newDays))
    }

    private fun updateState(state: ProductSubscribeUiState) {
        val updated = state.copy(
            totalPriceText = calculateTotal(state),
            daySummaryText = calculateSummary(state),
            simpleSummaryText = calculateSimpleSummary(state)
        )
        _uiState.value = updated
    }

    private fun calculateSimpleSummary(state: ProductSubscribeUiState): String {
        val qty = state.simpleQty
        val unit = if (qty > 1) "packets" else "packet"

        return when (state.selectedFrequency) {
            DeliveryFrequency.DAILY -> "$qty $unit × daily = ~${qty * 30}/month"
            DeliveryFrequency.ALTERNATE -> "$qty $unit × alternate days = ~${qty * 15}/month"
            DeliveryFrequency.MONTHLY -> "$qty $unit × monthly"
            else -> ""
        }
    }

    private fun calculateTotal(state: ProductSubscribeUiState): String {
        return "Subscribe Now "
    }

    private fun calculateSummary(state: ProductSubscribeUiState): String {
        return when (state.selectedFrequency) {
            DeliveryFrequency.WEEKLY, DeliveryFrequency.ALTERNATE -> {
                val totalPackets = state.dayStates.sumOf { if (it.isOn) it.qty else 0 }
                val selectedDaysCount = state.dayStates.count { it.isOn }
                val dayOrDays = if (selectedDaysCount == 1) "day" else "days"
                "$totalPackets packets on $selectedDaysCount $dayOrDays/cycle"
            }
            else -> ""
        }
    }
}
