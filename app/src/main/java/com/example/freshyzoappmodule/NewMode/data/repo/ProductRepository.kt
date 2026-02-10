package com.example.freshyzoappmodule.NewMode.data.repo

import com.example.freshyzoappmodule.NewMode.data.api.ApiService
import com.example.freshyzoappmodule.NewMode.data.model.ProductResponse

class ProductRepository(private val api: ApiService) {
    suspend fun getProducts(): List<ProductResponse> {
        return api.getProducts().products
    }

    suspend fun getProductDetails(id: Int): ProductResponse {
        return api.getProductDetail(id)
    }
}
