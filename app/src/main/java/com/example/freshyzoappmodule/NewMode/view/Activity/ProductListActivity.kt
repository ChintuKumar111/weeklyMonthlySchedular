package com.example.freshyzoappmodule.NewMode.view.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.NewMode.ViewModel.ProductViewModel
import com.example.freshyzoappmodule.NewMode.view.adapter.ProductAdapter
import com.example.freshyzoappmodule.databinding.ActivityMultipleImageBinding

class ProductListActivity : AppCompatActivity() {

    private val viewmodel : ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var binding: ActivityMultipleImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMultipleImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            val intent = Intent(this, ProductDetailsScreenActivity::class.java)
            intent.putExtra("id", product.id)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewmodel.productList.observe(this) { list ->
            adapter.submitList(list)
        }
    }
}
