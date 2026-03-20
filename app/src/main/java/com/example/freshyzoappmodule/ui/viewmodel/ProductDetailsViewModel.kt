package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.ProductDetails

class ProductDetailsViewModel : ViewModel() {
    private val _productDetails = MutableLiveData<ProductDetails>()
    val productDetails: LiveData<ProductDetails> = _productDetails

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int> = _qty

    private var count = 2

    init {
        _qty.value = count
    }

    fun setProduct(productDetails: ProductDetails) {
        _productDetails.value = productDetails
    }


    fun increaseQuantity() {
        count++
        _qty.value = count
    }

    fun decreaseQuantity() {
        if (count > 2) {
            count--
            _qty.value = count
        }
    }
}

