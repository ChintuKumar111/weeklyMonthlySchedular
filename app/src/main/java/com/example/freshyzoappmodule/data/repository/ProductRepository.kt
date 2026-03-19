package com.example.freshyzoappmodule.data.repository

import com.example.freshyzoappmodule.data.api.ApiService
import com.example.freshyzoappmodule.data.model.ProductDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(private val api: ApiService){
    fun getProducts(callback: (List<ProductDetails>?) -> Unit) {

        api.getProducts()
            .enqueue(object : Callback<List<ProductDetails>> {
                override fun onResponse(call: Call<List<ProductDetails>>,
                                        response: Response<List<ProductDetails>>
                ) {
                    callback(response.body())
                }

                override fun onFailure(
                    call: Call<List<ProductDetails>>,
                    t: Throwable
                ) {
                    callback(null)
                }
            })
    }
}
