package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Category
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.ProductSize
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding
import com.example.freshyzoappmodule.ui.adapter.CategoryAdapter
import com.example.freshyzoappmodule.ui.adapter.ProductAdapterr

class ProductSectionFragment : Fragment() {

    private var _binding: FragmentProductSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapterr
    private var cartTotal = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductRecyclerView()
        setupCategories()
        setupProducts(categoryId = 1) // Default to Milk
    }

    private fun setupCategories() {
        val categories = listOf(
            Category(1, "Milk", R.drawable.milk_),
            Category(2, "Ghee", R.drawable.ghee),
            Category(3, "Dahi", R.drawable.dahi),
            Category(4, "Paneer", R.drawable.paneer),
            Category(5, "Honey", R.drawable.dahi),
            Category(6, "Khoya", R.drawable.khowa),
        )

        categoryAdapter = CategoryAdapter(categories) { category, _ ->
            setupProducts(category.id)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun setupProducts(categoryId: Int) {
        val allProducts = getSampleProducts()
        val filtered = allProducts.filter { it.categoryId == categoryId }
        productAdapter.submitList(filtered)
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapterr(
            onAddClick = { product, size, qty ->
                cartTotal += size.price * qty
//                binding.tvCartAmount.text = "₹$cartTotal"
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

    private fun getSampleProducts(): List<Product> = listOf(
        // ── Milk (categoryId = 1) ──
        Product(
            id = 1,
            name = "FreshyZo A2 Cow Milk",
            tag = "100% Natural",
            description = "Pure, digestible & healthy",
            imageRes = R.drawable.milk_,
            badgeText = "A2",
            categoryId = 1,
            sizes = listOf(
                ProductSize("500ml", 45, 50),
                ProductSize("1 Litre", 80, 85)
            )
        ),
        Product(
            id = 2,
            name = "Buffalo Milk",
            tag = "Rich & Creamy",
            description = "High fat, ultra-creamy milk",
            imageRes = R.drawable.milk_,
            badgeText = "",
            categoryId = 1,
            sizes = listOf(
                ProductSize("500ml", 60, 70),
                ProductSize("1 Litre", 110, 130)
            )
        ),

        // ── Ghee (categoryId = 2) ──
        Product(
            id = 3,
            name = "Pure Cow Ghee",
            tag = "Traditional Recipe",
            description = "Slow-churned, farm-fresh desi ghee",
            imageRes = R.drawable.ghee,
            badgeText = "PURE",
            categoryId = 2,
            hasVip = true,
            vipSavingText = "Save ₹130 (20% OFF) with VIP",
            sizes = listOf(
                ProductSize("200gm", 520, 650),
                ProductSize("500gm", 1200, 1500),
                ProductSize("1kg", 2200, 2800)
            )
        ),

        // ── Dahi (categoryId = 3) ──
        Product(
            id = 4,
            name = "Malai Dahi",
            tag = "Probiotic",
            description = "Thick, creamy set curd",
            imageRes = R.drawable.dahi,
            badgeText = "",
            categoryId = 3,
            sizes = listOf(
                ProductSize("200gm", 35, 40),
                ProductSize("400gm", 65, 75)
            )
        ),
        Product(
            id = 5,
            name = "Khatti Dahi",
            tag = "Classic",
            description = "Traditional tangy curd",
            imageRes = R.drawable.dahi,
            badgeText = "",
            categoryId = 3,
            sizes = listOf(
                ProductSize("200gm", 28, 32),
                ProductSize("400gm", 52, 60)
            )
        ),

        // ── Paneer (categoryId = 4) ──
        Product(
            id = 6,
            name = "Fresh Paneer",
            tag = "Soft & Fresh",
            description = "Made fresh daily, no preservatives",
            imageRes = R.drawable.paneer,
            badgeText = "FRESH",
            categoryId = 4,
            sizes = listOf(
                ProductSize("200gm", 80, 95),
                ProductSize("500gm", 190, 220)
            )
        ),

        // ── Honey (categoryId = 5) ──
        Product(
            id = 7,
            name = "Wild Forest Honey",
            tag = "100% Natural",
            description = "German Lab Certified, pure wild honey",
            imageRes = R.drawable.dahi,
            badgeText = "WILD",
            categoryId = 5,
            hasVip = true,
            vipSavingText = "Save ₹84 (20% OFF) with VIP",
            sizes = listOf(
                ProductSize("250gm", 220, 260),
                ProductSize("475gm", 380, 420)
            )
        ),

        // ── Khoya (categoryId = 6) ──
        Product(
            id = 8,
            name = "Fresh Khoya",
            tag = "Homestyle",
            description = "Rich, thick mawa for sweets",
            imageRes = R.drawable.khowa,
            badgeText = "",
            categoryId = 6,
            sizes = listOf(
                ProductSize("200gm", 120, 140),
                ProductSize("500gm", 280, 330)
            )
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
