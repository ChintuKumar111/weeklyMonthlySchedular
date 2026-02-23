package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.DayState
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency

class ProductSubscribeViewModel : ViewModel() {

    private val basePrice = 70

    private val _uiState = MutableLiveData(ProductSubscribeUiState())
    val uiState: LiveData<ProductSubscribeUiState> = _uiState

    fun increaseSimpleQty() {
        val current = _uiState.value ?: return
        if (current.simpleQty < 10) {
            updateState(current.copy(simpleQty = current.simpleQty + 1))
        }
    }

    fun decreaseSimpleQty() {
        val current = _uiState.value ?: return
        if (current.simpleQty > 1) {
            updateState(current.copy(simpleQty = current.simpleQty - 1))
        }
    }

    fun selectFrequency(freq: DeliveryFrequency) {
        val current = _uiState.value ?: return
        updateState(current.copy(selectedFrequency = freq))
    }

    private fun updateState(state: ProductSubscribeUiState) {

        val totalPrice = when (state.selectedFrequency) {
            DeliveryFrequency.DAILY,
            DeliveryFrequency.MONTHLY ->
                state.simpleQty * basePrice

            else -> state.simpleQty * basePrice
        }

        val newState = state.copy(
            totalPriceText = "Subscribe Now · ₹$totalPrice",
            simpleSummaryText =
                "${state.simpleQty} packet(s) × ${state.selectedFrequency.label}"
        )

        _uiState.value = newState
    }
}