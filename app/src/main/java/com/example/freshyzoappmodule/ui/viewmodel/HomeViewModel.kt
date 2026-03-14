package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.repository.ProductRepository

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> = _productList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProducts() {
        if (!_productList.value.isNullOrEmpty()) {
            return
        }

        _isLoading.value = true
        repository.getProducts { results ->
            _isLoading.value = false
            if (results != null) {
                _productList.value = results
            } else {
                _errorMessage.value = "Failed to load products. Please check your connection."
            }
        }
    }
}
