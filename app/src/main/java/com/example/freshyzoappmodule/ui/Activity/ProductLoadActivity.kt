package com.example.freshyzoappmodule.ui.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.databinding.ActivityProductLoadActivityBinding
import com.example.freshyzoappmodule.ui.Adapter.ProductAdapter
import com.example.freshyzoappmodule.viewmodel.ProductLoadViewModel

class ProductLoadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductLoadActivityBinding
    private val viewModel: ProductLoadViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductLoadActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeData()
        viewModel.loadProducts()

        binding.btnSearchBar.setOnClickListener {
            val list = viewModel.productList.value
            if (list.isNullOrEmpty()) {
                Toast.makeText(this, "Products not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putParcelableArrayListExtra("product_list", ArrayList(list))
            startActivity(intent)
        }
    }

    private fun setupUI() {
        adapter = ProductAdapter(emptyList())
        binding.rvProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduct.adapter = adapter
    }

    private fun observeData() {
        binding.progressCircular.visibility = View.VISIBLE
        viewModel.productList.observe(this) { list ->
            adapter.updateList(list)
            if (list.isNotEmpty()) {
                Toast.makeText(this, "Products updated", Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        
        // Optional: observe isLoading to show a progress bar if you have one in the layout
    }
}
