package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.CartState
import com.example.freshyzoappmodule.data.model.PopularProductModel
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ActivitySearchBinding
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.ui.adapter.PopularProductAdapter
import com.example.freshyzoappmodule.ui.adapter.ProductAdapter
import com.example.freshyzoappmodule.ui.adapter.RecentSearchAdapter
import com.example.freshyzoappmodule.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.concurrent.thread

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentAdapter: RecentSearchAdapter
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var cartRepository: CartRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)
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
        binding.cartPreview.setOnViewCartClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("OPEN_CART", true)
            intent.putExtra("FROM_SEARCH", true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartState()
        syncProductQuantities()
    }

    private fun syncProductQuantities() {
        val sharedQuantities = cartRepository.getCartState()?.productQuantities ?: emptyMap()
        productAdapter.setInitialQuantities(sharedQuantities)
    }

    private fun updateUIState(
        list: List<Product>,
        isLoading: Boolean,
        query: String,
        recentList: List<String>
    ) {
        when {
            isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility = View.GONE
            }

            query.isEmpty() -> {
                binding.progressBar.visibility = View.GONE
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility =
                    if (recentList.isNotEmpty()) View.VISIBLE else View.GONE
            }

            list.isEmpty() -> {
                binding.progressBar.visibility = View.GONE
                binding.rvSearch.visibility = View.GONE
                binding.llNoMatch.visibility = View.VISIBLE
                binding.llRecentSearch.visibility = View.GONE
            }

            else -> {
                binding.progressBar.visibility = View.GONE
                binding.rvSearch.visibility = View.VISIBLE
                binding.llNoMatch.visibility = View.GONE
                binding.llRecentSearch.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {

        viewModel.filteredList.observe(this) { list ->

            val sharedQuantities =
                cartRepository.getCartState()?.productQuantities ?: emptyMap()
            productAdapter.setInitialQuantities(sharedQuantities)

            productAdapter.submitList(list)

            updateUIState(
                list = list,
                isLoading = viewModel.isLoading.value ?: false,
                query = binding.etSearch.text.toString().trim(),
                recentList = viewModel.recentSearches.value ?: emptyList()
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            updateUIState(
                list = viewModel.filteredList.value ?: emptyList(),
                isLoading = isLoading,
                query = binding.etSearch.text.toString().trim(),
                recentList = viewModel.recentSearches.value ?: emptyList()
            )
        }

        viewModel.recentSearches.observe(this) { list ->
            recentAdapter.updateList(list)

            updateUIState(
                list = viewModel.filteredList.value ?: emptyList(),
                isLoading = viewModel.isLoading.value ?: false,
                query = binding.etSearch.text.toString().trim(),
                recentList = list
            )
        }

        viewModel.isHintVisible.observe(this) { isVisible ->
            binding.textSwitcher.visibility =
                if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.currentHintIndex.observe(this) { index ->
            binding.textSwitcher.setText(viewModel.getHintText(index))
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
        recentAdapter = RecentSearchAdapter(
            emptyList(),
            onItemClick = { selectedText ->
                binding.etSearch.setText(selectedText)
                binding.etSearch.setSelection(selectedText.length)
            },
            onDeleteClick = { textToDelete ->
                viewModel.deleteRecentSearch(textToDelete)
            }
        )
        binding.rvRecentSearches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecentSearches.adapter = recentAdapter
    }

    private fun setupSearchProducts() {
        productAdapter = ProductAdapter(
            onAddClick = { product, size, qty ->
                updateCart(product, size.price.toDouble() * qty, qty)
            },
            onQtyChange = { product, size, delta ->
                updateCart(product, size.price.toDouble() * delta, delta)
            },
            onSubscribeClick = { product ->
                Toast.makeText(this, "Subscribed to ${product.productName}", Toast.LENGTH_SHORT).show()
            },
            onProductClick = { product ->
               val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = productAdapter
    }

    private fun updateCart(product: Product, priceDelta: Double, countDelta: Int) {
        thread {
            val currentState = cartRepository.getCartState() ?: CartState()
            val newCount = currentState.itemsCount + countDelta
            val newPrice = currentState.totalPrice + priceDelta

            val productId = product.id
            val newQuantities = currentState.productQuantities.toMutableMap()
            val currentQty = newQuantities[productId] ?: 0
            val newQty = currentQty + countDelta

            val currentProducts = currentState.products.toMutableList()

            if (newQty <= 0) {
                newQuantities.remove(productId)
                currentProducts.removeAll { it.id == productId }
            } else {
                newQuantities[productId] = newQty
                if (!currentProducts.any { it.id == productId }) {
                    currentProducts.add(product)
                }
            }

            val newState = CartState(newCount, newPrice, true, newQuantities, currentProducts)
            cartRepository.saveCartState(newState)

            runOnUiThread {
                if (newState.itemsCount > 0) {
                    binding.cartPreview.showCart(newState)
                } else {
                    binding.cartPreview.hideCart()
                }
            }
        }
    }

    private fun loadCartState() {
        thread {
            val savedCartState = cartRepository.getCartState()
            runOnUiThread {
                if (savedCartState != null && savedCartState.itemsCount > 0) {
                    binding.cartPreview.showCart(savedCartState)
                } else {
                    binding.cartPreview.hideCart()
                }
            }
        }
    }

    private fun loadInitialData() {
        val incomingList = intent.getParcelableArrayListExtra<Product>("product_list")
        incomingList?.let { viewModel.setInitialProductList(it) }
    }

    private fun setUpUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}
