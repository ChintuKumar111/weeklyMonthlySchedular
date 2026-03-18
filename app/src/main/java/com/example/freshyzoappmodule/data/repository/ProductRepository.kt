package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(private val api: ApiService){
    fun getProducts(callback: (List<Product>?) -> Unit) {

        api.getProducts()
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
