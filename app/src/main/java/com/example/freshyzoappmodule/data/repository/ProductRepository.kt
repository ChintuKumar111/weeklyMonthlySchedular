package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.RetrofitClient
import com.example.freshyzoappmodule.data.model.ProductModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository {

    fun getProducts(callback: (List<ProductModel>?) -> Unit) {

        RetrofitClient.api.getProducts()
            .enqueue(object : Callback<List<ProductModel>> {
                override fun onResponse( call: Call<List<ProductModel>>,
                    response: Response<List<ProductModel>>
                ) {
                    callback(response.body())
                }

                override fun onFailure(
                    call: Call<List<ProductModel>>,
                    t: Throwable
                ) {
                    callback(null)
                }
            })
    }
}
