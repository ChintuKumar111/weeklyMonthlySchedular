package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.DayState
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency

class ProductSubscribeViewModel : ViewModel() {
    private var basePrice: Int = 0

    private val alternateDayIndices = listOf(0, 2, 4, 6)
    private val _uiState = MutableLiveData(ProductSubscribeUiState())
    val uiState: LiveData<ProductSubscribeUiState> = _uiState

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

            // 🔥 ALTERNATE → Select Mon Wed Fri Sun
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
        val state = _uiState.value!!
        if (state.simpleQty < 10) {
            updateState(state.copy(simpleQty = state.simpleQty + 1))
        }
    }

    fun decreaseSimpleQty() {
        val state = _uiState.value!!
        if (state.simpleQty > 1) {
            updateState(state.copy(simpleQty = state.simpleQty - 1))
        }
    }

    fun setBasePrice(price: Int) {
        basePrice = price
        updateState(_uiState.value ?: ProductSubscribeUiState())
    }

    fun toggleDay(index: Int) {
        val state = _uiState.value!!
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
        val state = _uiState.value!!
        val newDays = state.dayStates.mapIndexed { i, day ->
            if (i == index && day.isOn && day.qty < 10)
                day.copy(qty = day.qty + 1)
            else day
        }
        updateState(state.copy(dayStates = newDays))
    }

    fun decreaseDayQty(index: Int) {
        val state = _uiState.value!!
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
            daySummaryText = calculateSummary(state)
        )
        _uiState.value = updated
    }


    private fun calculateTotal(state: ProductSubscribeUiState): String {
        return when (state.selectedFrequency) {
            // For Weekly and Alternate, the logic is the same: sum up selected days.
            DeliveryFrequency.WEEKLY, DeliveryFrequency.ALTERNATE -> {
                val total = state.dayStates.sumOf { day ->
                    if (day.isOn) day.qty * basePrice else 0
                }
                "Subscribe Now "
            }

            // The 'Once' or 'Daily' calculation remains the same.
            else -> "Subscribe Now · ₹${state.simpleQty * basePrice}"
        }
    }

    private fun calculateSummary(state: ProductSubscribeUiState): String {
        return when (state.selectedFrequency) {
            DeliveryFrequency.WEEKLY, DeliveryFrequency.ALTERNATE -> {
                // Correctly calculate summary based on the actual state of the days.
                val totalPackets = state.dayStates.sumOf { if (it.isOn) it.qty else 0 }
                val selectedDaysCount = state.dayStates.count { it.isOn }
                val dayOrDays = if (selectedDaysCount == 1) "day" else "days"
                "$totalPackets packets on $selectedDaysCount $dayOrDays/cycle"
            }
            // Return an empty or default summary for other frequencies.
            else -> ""
        }
    }

}