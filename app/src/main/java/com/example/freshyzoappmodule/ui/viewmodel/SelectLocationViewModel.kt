package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.repository.GeocoderRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SelectLocationViewModel(
    private val repository: GeocoderRepository
) : ViewModel() {

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    fun fetchAddress(latLng: LatLng) {
        viewModelScope.launch {
            _address.value = "Fetching address..."
            val result = repository.getAddress(latLng)
            _address.value = result
        }
    }
}