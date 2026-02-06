package com.example.freshyzoappmodule.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.compose.ComposeActivity
import com.example.freshyzoappmodule.databinding.ActivitySearchActivityyBinding
import com.example.freshyzoappmodule.search.Api.RetrofitClient
import com.example.freshyzoappmodule.search.adapter.ProductAdapter
import com.example.freshyzoappmodule.search.model.ProductModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Parcelable



class ProductLoadActivity : AppCompatActivity() {

    var productList = ArrayList<ProductModel>()
    lateinit var binding: ActivitySearchActivityyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchActivityyBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnSearchBar.setOnClickListener{

            if(productList.isEmpty()){
                Toast.makeText(this,"Products not loaded yet",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putParcelableArrayListExtra("product_list", productList)
            startActivity(intent)
        }

        setUpUi()
        apiCalling()


    }





    fun setUpUi(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun apiCalling(){

        RetrofitClient.api.getProducts().enqueue(object :Callback<List<ProductModel>>

        {

            override fun onResponse(
                call: Call<List<ProductModel>>,
                response: Response<List<ProductModel>>
            ) {

                if (response.isSuccessful) {
                    productList.clear()
                    productList.addAll(response.body()!!)

                    Toast.makeText( this@ProductLoadActivity," success fetching data ",Toast.LENGTH_SHORT).show()

                    binding.rvProduct.layoutManager =
                        GridLayoutManager(this@ProductLoadActivity, 2)

                    binding.rvProduct.adapter =
                        ProductAdapter(productList)
                }
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                Toast.makeText( this@ProductLoadActivity,"API Failed",Toast.LENGTH_SHORT).show()
            }
        })
    }
}
