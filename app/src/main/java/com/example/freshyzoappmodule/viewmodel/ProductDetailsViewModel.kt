package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.Product

class ProductDetailsViewModel : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    fun setProduct(product: Product) {
        _product.value = product
    }
}
