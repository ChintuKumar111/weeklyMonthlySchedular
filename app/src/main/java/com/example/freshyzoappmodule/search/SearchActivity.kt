package com.example.freshyzoappmodule.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivitySearchBinding
import com.example.freshyzoappmodule.search.adapter.PopularAdapter
import com.example.freshyzoappmodule.search.adapter.ProductAdapter
import com.example.freshyzoappmodule.search.model.PopularProduct
import com.example.freshyzoappmodule.search.model.ProductModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<ProductModel>()
    private var filteredList = ArrayList<ProductModel>()
    
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        setupPopularProducts()
        setupSearchProducts()
        
        loadProducts()

        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            
            // Cancel any pending search
            searchRunnable?.let { searchHandler.removeCallbacks(it) }
            
            if (query.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                filterProducts(query)
            } else {
                // Show progress bar and hide results/no-match while "loading"
                binding.progressBar.visibility = View.VISIBLE
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
                
                searchRunnable = Runnable {
                    filterProducts(query)
                    binding.progressBar.visibility = View.GONE
                }
                searchHandler.postDelayed(searchRunnable!!, 1000) // 1 second delay
            }
        }
    }

    private fun setupPopularProducts() {
        val products = listOf(
            PopularProduct("Milk", R.drawable.milk),
            PopularProduct("Ghee", R.drawable.ghee),
            PopularProduct("Khowa", R.drawable.khowa),
            PopularProduct("Paneer", R.drawable.paneer),
            PopularProduct("Dahi", R.drawable.dahi),


        )

        binding.rvPopularProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPopularProducts.adapter = PopularAdapter(products) { clickedProduct ->
            binding.etSearch.setText(clickedProduct.name)
            binding.etSearch.setSelection(clickedProduct.name.length)
            // No need to call filterProducts here as setText triggers textChangedListener
        }
    }

    private fun setupSearchProducts() {
        adapter = ProductAdapter(filteredList)
        binding.rvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearch.adapter = adapter
    }

    private fun loadProducts() {
        val incomingList = intent.getParcelableArrayListExtra<ProductModel>("product_list")
        if (incomingList != null) {
            productList.clear()
            productList.addAll(incomingList)
        }
    }

    private fun setUpUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun filterProducts(query: String) {
        filteredList.clear()

        // Always keep Popular section visible
        binding.tvPopularHeader.visibility = View.VISIBLE
        binding.rvPopularProducts.visibility = View.VISIBLE

        if (query.isEmpty()) {
            binding.rvSearch.visibility = View.GONE
            binding.llNoMatch.visibility = View.GONE
        } else {
            for (product in productList) {
                if (product.product_name.contains(query, true)) {
                    filteredList.add(product)
                }
            }

            if (filteredList.isEmpty()) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.VISIBLE
            } else {
                binding.rvSearch.visibility = View.VISIBLE
                binding.llNoMatch.visibility = View.GONE
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handler to prevent memory leaks
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }
}
