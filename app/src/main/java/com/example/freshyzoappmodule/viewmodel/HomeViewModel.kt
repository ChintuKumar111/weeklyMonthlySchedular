package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.data.repository.ProductRepository

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productList = MutableLiveData<List<ProductModel>>()
    val productList: LiveData<List<ProductModel>> = _productList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProducts() {
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
