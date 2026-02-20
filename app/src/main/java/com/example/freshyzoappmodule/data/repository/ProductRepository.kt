package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.RetrofitClient
import com.example.freshyzoappmodule.data.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository {
    fun getProducts(callback: (List<Product>?) -> Unit) {

        RetrofitClient.api.getProducts()
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse( call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {
                    callback(response.body())
                }

                override fun onFailure(
                    call: Call<List<Product>>,
                    t: Throwable
                ) {
                    callback(null)
                }
            })
    }
}
