package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Category
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.ProductSize
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity
import com.example.freshyzoappmodule.ui.activity.SearchActivity
import com.example.freshyzoappmodule.ui.adapter.CategoryAdapter
import com.example.freshyzoappmodule.ui.adapter.ProductAdapterr
import com.example.freshyzoappmodule.viewmodel.HomeViewModel
import kotlin.concurrent.thread

class ProductSectionFragment : Fragment() {
    private var _binding: FragmentProductSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapterr
    private val viewModel: HomeViewModel by viewModels()

    private var allProducts: List<Product> = emptyList()
    private var selectedCategoryId: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductRecyclerView()
        setupCategories()
        observeViewModel()
        
        // Delay heavy operations slightly to ensure fragment transition is smooth
        view.post {
            viewModel.loadProducts()
        }

        binding.btnSearch.setOnClickListener {
            val list = viewModel.productList.value
            if (list.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Products not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), SearchActivity::class.java)
            intent.putParcelableArrayListExtra("product_list", ArrayList(list))
            startActivity(intent)
        }
        
        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupCategories() {
        val categories = listOf(
            Category(1, "Milk", R.drawable.milk_),
            Category(2, "Ghee", R.drawable.ghee1),
            Category(3, "Dahi", R.drawable.dahi),
            Category(4, "Paneer", R.drawable.paneer),
            Category(6, "Khowa", R.drawable.khowa),
        )

        categoryAdapter = CategoryAdapter(categories) { category, _ ->
            selectedCategoryId = category.id
            filterProducts()
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.productList.observe(viewLifecycleOwner, Observer { products ->
            // Offload data processing to background thread to prevent UI stutter
            thread {
                val mapped = products.map {
                    val (tag, badge) = when {
                        it.product_name.contains("Buffalo Milk", true) -> "Rich & Creamy" to ""
                        it.product_name.contains("A2 Cow Milk", true) -> "100% Natural" to "A2"
                        it.product_name.contains("Pure Cow Ghee", true) -> "Traditional Recipe" to "PURE"
                        it.product_name.contains("Malai Dahi", true) -> "Probiotic" to ""
                        it.product_name.contains("Khatti Dahi", true) -> "Classic" to ""
                        it.product_name.contains("Fresh Paneer", true) -> "Soft & Fresh" to "FRESH"
                        it.product_name.contains("Khoya", true) || it.product_name.contains("Khowa", true) -> "Homestyle" to ""
                        else -> "100% Natural" to ""
                    }

                    val catId = when {
                        it.product_name.contains("Ghee", true) -> 2
                        it.product_name.contains("Milk", true) -> 1
                        it.product_name.contains("Dahi", true) -> 3
                        it.product_name.contains("Paneer", true) -> 4
                        it.product_name.contains("Khowa", true) ||
                                it.product_name.contains("Khoya", true) -> 6
                        else -> 1
                    }

                    val words = it.product_name.trim().split(" ")
                    val sizeFromTitle = if (words.size > 2) {
                        "${words[words.size - 2]} ${words.last()}"
                    } else {
                        it.unit
                    }

                    Product(
                        id = it.product_id.toIntOrNull() ?: 0,
                        name = it.product_name,
                        tag = tag,
                        description = it.description,
                        short_description = it.short_desc,
                        imageUrl = "https://freshyzo.com/admin/uploads/product_image/${it.dairy_product_image}",
                        badgeText = badge,
                        categoryId = catId,
                        sizes = listOf(
                            ProductSize(
                                sizeFromTitle,
                                it.product_price.toDoubleOrNull()?.toInt() ?: 0,
                                it.dairy_mrp.toDoubleOrNull()?.toInt() ?: 0
                            )
                        )
                    )
                }

                activity?.runOnUiThread {
                    if (_binding != null) {
                        allProducts = mapped
                        filterProducts()
                    }
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun filterProducts() {
        val filtered = allProducts.filter { it.categoryId == selectedCategoryId }
        
        // Pass the saved quantities from Activity to Adapter
        val sharedQuantities = (activity as? NewHomeActivity)?.getCartState()?.productQuantities ?: emptyMap()
        productAdapter.setInitialQuantities(sharedQuantities)
        
        productAdapter.submitList(filtered)
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapterr(
            onAddClick = { product, size, qty ->
                (activity as? NewHomeActivity)?.updateSharedCart(product.id, size.price.toDouble() * qty, qty)
            },
            onQtyChange = { product, size, delta ->
                (activity as? NewHomeActivity)?.updateSharedCart(product.id, size.price.toDouble() * delta, delta)
            },
            onSubscribeClick = { product ->
                Toast.makeText(requireContext(), "Subscribed to ${product.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
