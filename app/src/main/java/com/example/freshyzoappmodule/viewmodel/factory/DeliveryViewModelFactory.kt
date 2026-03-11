package com.example.freshyzoappmodule.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freshyzoappmodule.data.repository.DeliveryRepository
import com.example.freshyzoappmodule.viewmodel.DeliveryViewModel

class DeliveryViewModelFactory(private val repository: DeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeliveryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
