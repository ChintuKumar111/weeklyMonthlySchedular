package com.example.freshyzoappmodule.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freshyzoappmodule.data.repository.OrderHistoryRepository
import com.example.freshyzoappmodule.viewmodel.OrderHistoryViewModel

class OrderHistoryViewModelFactory(private val repository: OrderHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
