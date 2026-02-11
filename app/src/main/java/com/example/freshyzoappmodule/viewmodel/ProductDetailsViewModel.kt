package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.ProductModel

class ProductDetailsViewModel : ViewModel() {
    private val _product = MutableLiveData<ProductModel>()
    val product: LiveData<ProductModel> = _product

    fun setProduct(product: ProductModel) {
        _product.value = product
    }
}
