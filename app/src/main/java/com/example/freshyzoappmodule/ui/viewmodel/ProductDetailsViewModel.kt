package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.freshyzoappmodule.data.model.Product

class ProductDetailsViewModel : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int> = _qty

    private var count = 1


    fun setProduct(product: Product) {
        _product.value = product
    }


    fun increaseQuantity() {
        count++
        _qty.value = count
    }

    fun decreaseQuantity() {
        if (count > 1) {
            count--
            _qty.value = count
        }
    }
}

