package com.example.freshyzoappmodule.viewmodel

import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.Product
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_PackageNameFactory.packageName
import java.io.File
import java.io.FileOutputStream

class ProductDetailsViewModel : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int> = _qty

    private var count = 0

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
