package com.shyamdairyfarm.user.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shyamdairyfarm.user.data.model.ProductDetails
import com.shyamdairyfarm.user.data.repository.ProductRepository

class HomeViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _productDetailsList = MutableLiveData<List<ProductDetails>>()
    val productDetailsList: LiveData<List<ProductDetails>> = _productDetailsList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProducts() {
        if (!_productDetailsList.value.isNullOrEmpty()) {
            return
        }

        _isLoading.value = true
        repository.getProducts { results ->
            _isLoading.value = false
            if (results != null) {
                _productDetailsList.value = results
            } else {
                _errorMessage.value = "Failed to load products. Please check your connection."
            }
        }
    }
}
