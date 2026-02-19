package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.freshyzoappmodule.ui.activity.SearchActivity
import com.example.freshyzoappmodule.ui.adapter.CategoryAdapter
import com.example.freshyzoappmodule.ui.adapter.ProductAdapterr
import com.example.freshyzoappmodule.viewmodel.HomeViewModel

class ProductSectionFragment : Fragment() {
    private var _binding: FragmentProductSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapterr
    private var cartTotal = 0
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
        viewModel.loadProducts()

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
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
            allProducts = products.map {
                Log.d("CATEGORY_DEBUG", "API Category Name: ${it.product_category_name}")

                // Determine tag and badge
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

                // Determine Category ID
                val catId = when {
                    it.product_name.contains("Ghee", true) -> 2
                    it.product_name.contains("Milk", true) -> 1
                    it.product_name.contains("Dahi", true) -> 3
                    it.product_name.contains("Paneer", true) -> 4
                    it.product_name.contains("Khowa", true) ||
                            it.product_name.contains("Khoya", true) -> 6
                    else -> 1
                }


                Log.d("CATEGORY_DEBUG", "Assigned categoryId: $catId")
                Log.d("CATEGORY_DEBUG", "API Category Name: ${it.product_category_name}")


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
                            it.unit,
                            it.product_price.toDoubleOrNull()?.toInt() ?: 0,
                            it.dairy_mrp.toDoubleOrNull()?.toInt() ?: 0
                        )
                    )
                )
            }
            filterProducts()
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
        Log.d("FILTER_DEBUG", "Filtered size: ${filtered.size}")
        productAdapter.submitList(filtered)
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapterr(
            onAddClick = { product, size, qty ->
                cartTotal += size.price * qty
                Toast.makeText(requireContext(), "${product.name} (${size.label}) × $qty added!", Toast.LENGTH_SHORT).show()
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
