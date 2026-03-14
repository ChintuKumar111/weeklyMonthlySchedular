package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.Address
import com.example.freshyzoappmodule.data.repository.AddressRepository
import kotlinx.coroutines.launch

// In AddressViewModel.kt
class AddressViewModel(private val repository: AddressRepository) : ViewModel() {

  // for address update




    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address> = _address

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadSavedAddress() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSavedAddress().onSuccess {
                _address.value = it
            }.onFailure {
                _error.value = it.message ?: "Failed to load address"
            }
            _isLoading.value = false
        }
    }

    fun updateAddress(newAddress: Address) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateAddress(newAddress).onSuccess {
                _address.value = it
            }.onFailure {
                _error.value = it.message ?: "Failed to update address"
            }
            _isLoading.value = false
        }
    }

    fun updateAddress(fullAddress: String, lat: Double = 0.0, lng: Double = 0.0) {
        val current = _address.value ?: Address()
        val updated = current.copy(fullAddress = fullAddress, lat = lat, lng = lng)
        updateAddress(updated)
    }
}
