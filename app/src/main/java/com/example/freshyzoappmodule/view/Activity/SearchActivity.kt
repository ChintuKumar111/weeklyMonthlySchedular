package com.example.freshyzoappmodule.view.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivitySearchBinding
import com.example.freshyzoappmodule.data.model.PopularProductModel
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.view.Adapter.PopularProductAdapter
import com.example.freshyzoappmodule.view.Adapter.ProductAdapter
import com.example.freshyzoappmodule.view.Adapter.RecentSearchAdapter
import com.example.freshyzoappmodule.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentAdapter: RecentSearchAdapter
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        setupTextSwitcher()
        setupPopularProducts()
        setupRecentSearches()
        setupSearchProducts()
        
        loadInitialData()
        observeViewModel()

        binding.etSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text.toString().trim())
        }
    }

    private fun observeViewModel() {
        viewModel.filteredList.observe(this) { list ->
            productAdapter.updateList(list)
            
            val query = binding.etSearch.text.toString().trim()
            if (query.isEmpty()) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility = if (viewModel.recentSearches.value?.isNotEmpty() == true) View.VISIBLE else View.GONE
            } else if (list.isEmpty() && viewModel.isLoading.value == false) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.VISIBLE
                binding.llRecentSearch.visibility = View.GONE
            } else {
                binding.rvSearch.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility = View.GONE
            }
        }

        viewModel.isHintVisible.observe(this) { isVisible ->
            binding.textSwitcher.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.currentHintIndex.observe(this) { index ->
            binding.textSwitcher.setText(viewModel.getHintText(index))
        }
        
        viewModel.showNoMatch.observe(this) { show ->
            if (show && binding.etSearch.text.toString().trim().isNotEmpty()) {
                binding.llNoMatch.visibility = View.VISIBLE
                binding.rvSearch.visibility = View.GONE
            } else {
                binding.llNoMatch.visibility = View.GONE
            }
        }

        viewModel.recentSearches.observe(this) { list ->
            recentAdapter.updateList(list)
            if (binding.etSearch.text.toString().trim().isEmpty() && list.isNotEmpty()) {
                binding.llRecentSearch.visibility = View.VISIBLE
            } else {
                binding.llRecentSearch.visibility = View.GONE
            }
        }
    }

    private fun setupTextSwitcher() {
        binding.textSwitcher.setFactory {
            TextView(this@SearchActivity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.GRAY)
                textSize = 14f
            }
        }
    }

    private fun setupPopularProducts() {
        val products = listOf(
            PopularProductModel("Milk", R.drawable.milk),
            PopularProductModel("dahi", R.drawable.dahi),
            PopularProductModel("Paneer", R.drawable.paneer),
            PopularProductModel("ghee", R.drawable.ghee),
            PopularProductModel("khowa", R.drawable.khowa)
        )

        binding.rvPopularProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPopularProducts.adapter = PopularProductAdapter(products) { clickedProduct ->
            binding.etSearch.setText(clickedProduct.name)
            binding.etSearch.setSelection(clickedProduct.name.length)
        }
    }

    private fun setupRecentSearches() {
        recentAdapter = RecentSearchAdapter(emptyList(), 
            onItemClick = { selectedText ->
                binding.etSearch.setText(selectedText)
                binding.etSearch.setSelection(selectedText.length)
            },
            onDeleteClick = { textToDelete ->
                viewModel.deleteRecentSearch(textToDelete)
            }
        )
        // Changed to HORIZONTAL layout manager
        binding.rvRecentSearches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecentSearches.adapter = recentAdapter
    }

    private fun setupSearchProducts() {
        productAdapter = ProductAdapter(emptyList()) { product, delta ->
            Toast.makeText(this, "${product.product_name} updated in cart", Toast.LENGTH_SHORT).show()
        }
        binding.rvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearch.adapter = productAdapter
    }

    private fun loadInitialData() {
        val incomingList = intent.getParcelableArrayListExtra<ProductModel>("product_list")
        incomingList?.let { viewModel.setInitialProductList(it) }
    }

    private fun setUpUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
