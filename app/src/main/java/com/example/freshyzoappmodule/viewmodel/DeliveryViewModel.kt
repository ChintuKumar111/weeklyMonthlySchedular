package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.DeliveryModel
import com.example.freshyzoappmodule.data.repository.DeliveryRepository
import kotlinx.coroutines.launch

class DeliveryViewModel(private val repository: DeliveryRepository) : ViewModel() {
    private val _deliveries = MutableLiveData<List<DeliveryModel>>()
    val deliveries: LiveData<List<DeliveryModel>> get() = _deliveries
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    fun fetchDeliveries() {
        // 1. Show dummy data immediately so the UI is populated instantly
        _deliveries.value = getDummyDeliveries()
        viewModelScope.launch {
            // 2. Start loading in background (for when you add the real API)
            _isLoading.value = true
            _error.value = null
            
            val result = repository.getDeliveries()
            result.onSuccess {
                // Only override dummy data if the API returns a non-empty list
                if (it.isNotEmpty()) {
                    _deliveries.value = it
                }
            }.onFailure {
                // If API fails, we keep the dummy data already shown.
                // You can optionally show an error message here.
                // _error.value = "Connect to API failed. Showing sample data."
            }
            _isLoading.value = false
        }
    }

    private fun getDummyDeliveries(): List<DeliveryModel> = listOf(
        DeliveryModel(
            id               = 1,
            productName      = "FreshyZo A2 Cow Milk",
            quantity         = "500 ml · Qty 2",
            productImageUrl  = "",
            status           = "Delivered",
            price            = 90.0,
            transactionId    = "370386",
            date             = "26 Feb 2026",
            remainingBalance = -1526.0,
            remark           = ""
        ),
        DeliveryModel(
            id               = 2,
            productName      = "FreshyZo Cow Ghee",
            quantity         = "1000 ml · Qty 1",
            productImageUrl  = "",
            status           = "Delivered",
            price            = 750.0,
            transactionId    = "370387",
            date             = "25 Feb 2026",
            remainingBalance = -1436.0,
            remark           = ""
        )
    )
}
