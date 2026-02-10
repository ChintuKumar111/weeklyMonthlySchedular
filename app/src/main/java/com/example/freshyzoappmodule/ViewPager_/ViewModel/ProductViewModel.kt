package com.example.freshyzoappmodule.ViewPager_.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.ViewPager_.data.api.RetrofitClient
import com.example.freshyzoappmodule.ViewPager_.data.model.ProductResponse
import com.example.freshyzoappmodule.ViewPager_.data.repo.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository =
        ProductRepository(
            RetrofitClient.api
        )

    private val _productList = MutableLiveData<List<ProductResponse>>()
    val productList: LiveData<List<ProductResponse>> get() = _productList

    private val _productDetail = MutableLiveData<ProductResponse?>()
    val productDetail: LiveData<ProductResponse?> get() = _productDetail

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val results = repository.getProducts()
                _productList.value = results
            } catch (e: Exception) {
                _productList.value = emptyList()
            }
        }
    }

    fun loadProductDetail(id: Int) {
        viewModelScope.launch {
            try {
                val detail = repository.getProductDetails(id)
                _productDetail.value = detail
            } catch (e: Exception) {
                _productDetail.value = null
            }
        }
    }
}
