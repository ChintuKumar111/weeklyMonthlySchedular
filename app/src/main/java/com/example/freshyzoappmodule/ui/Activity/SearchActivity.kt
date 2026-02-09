package com.example.freshyzoappmodule.ui.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
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
import com.example.freshyzoappmodule.ui.Adapter.PopularProductAdapter
import com.example.freshyzoappmodule.ui.Adapter.ProductAdapter
import com.example.freshyzoappmodule.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: ProductAdapter
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        setupTextSwitcher()
        setupPopularProducts()
        setupSearchProducts()
        
        loadInitialData()
        observeViewModel()

        binding.etSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text.toString().trim())
        }
    }

    private fun observeViewModel() {
        viewModel.filteredList.observe(this) { list ->
            adapter.updateList(list)
            
            // Toggle visibility of result list and No Match UI
            if (binding.etSearch.text.toString().trim().isEmpty()) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
            } else if (list.isEmpty() && !viewModel.isLoading.value!!) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.VISIBLE
            } else {
                binding.rvSearch.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                binding.llNoMatch.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
            }
        }

        viewModel.isHintVisible.observe(this) { isVisible ->
            binding.textSwitcher.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.currentHintIndex.observe(this) { index ->
            binding.textSwitcher.setText(viewModel.getHintText(index))
        }
        
        viewModel.showNoMatch.observe(this) { show ->
            if (show && !binding.etSearch.text.toString().trim().isEmpty()) {
                binding.llNoMatch.visibility = View.VISIBLE
                binding.rvSearch.visibility = View.GONE
            } else {
                binding.llNoMatch.visibility = View.GONE
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
            PopularProductModel("Butter", R.drawable.ghee),
            PopularProductModel("Paneer", R.drawable.paneer),
            PopularProductModel("dahi", R.drawable.dahi),
            PopularProductModel("ghee", R.drawable.ghee)
        )

        binding.rvPopularProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPopularProducts.adapter = PopularProductAdapter(products) { clickedProduct ->
            binding.etSearch.setText(clickedProduct.name)
            binding.etSearch.setSelection(clickedProduct.name.length)
        }
    }

    private fun setupSearchProducts() {
        adapter = ProductAdapter(emptyList())
        binding.rvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearch.adapter = adapter
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
