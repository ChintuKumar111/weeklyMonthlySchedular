package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.DeliveryStatus
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import com.example.freshyzoappmodule.data.repository.OrderHistoryRepository
import kotlinx.coroutines.launch

class OrderHistoryViewModel(private val repository: OrderHistoryRepository) : ViewModel() {

    private val _activeFilter = MutableLiveData<String>("all")
    val activeFilter: LiveData<String> = _activeFilter

    private var allOrders: List<OrderHistoryModel> = emptyList()

    private val _filteredDeliveries = MutableLiveData<List<OrderHistoryModel>>()
    val filteredDeliveries: LiveData<List<OrderHistoryModel>> = _filteredDeliveries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    data class DeliveryStats(
        val total: Int,
        val placed: Int,
        val pending: Int,
        val cancelled: Int
    )

    private val _stats = MutableLiveData<DeliveryStats>()
    val stats: LiveData<DeliveryStats> = _stats

    fun fetchOrderHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getOrderHistory()
            result.onSuccess {
                allOrders = it
                applyFilter(_activeFilter.value ?: "all")
                _stats.value = computeStats()
            }.onFailure {
                _error.value = it.message ?: "An unknown error occurred"
            }
            _isLoading.value = false
        }
    }

    fun applyFilter(filter: String) {
        _activeFilter.value = filter
        _filteredDeliveries.value = when (filter.lowercase()) {
            "placed"    -> allOrders.filter { it.status == DeliveryStatus.PLACED }
            "pending"   -> allOrders.filter { it.status == DeliveryStatus.PENDING }
            "cancelled" -> allOrders.filter { it.status == DeliveryStatus.CANCELLED }
            else        -> allOrders
        }
    }

    private fun computeStats() = DeliveryStats(
        total     = allOrders.size,
        placed    = allOrders.count { it.status == DeliveryStatus.PLACED },
        pending   = allOrders.count { it.status == DeliveryStatus.PENDING },
        cancelled = allOrders.count { it.status == DeliveryStatus.CANCELLED }
    )
}
