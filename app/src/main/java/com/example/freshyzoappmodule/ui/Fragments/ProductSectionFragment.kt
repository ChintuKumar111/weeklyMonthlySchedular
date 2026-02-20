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
import com.example.freshyzoappmodule.data.model.categoryModel
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity
import com.example.freshyzoappmodule.ui.activity.SearchActivity
import com.example.freshyzoappmodule.ui.adapter.CategoryAdapter
import com.example.freshyzoappmodule.ui.adapter.ProductAdapter
import com.example.freshyzoappmodule.viewmodel.HomeViewModel

class ProductSectionFragment : Fragment() {
    private var _binding: FragmentProductSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
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
            categoryModel(1, "Milk", R.drawable.milk_),
            categoryModel(2, "Ghee", R.drawable.ghee1),
            categoryModel(3, "Dahi", R.drawable.dahi),
            categoryModel(4, "Paneer", R.drawable.paneer),
            categoryModel(6, "Khowa", R.drawable.khowa),
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
            allProducts = products
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
        
        val sharedQuantities = (activity as? NewHomeActivity)?.getCartState()?.productQuantities ?: emptyMap()
        productAdapter.setInitialQuantities(sharedQuantities)
        
        productAdapter.submitList(filtered)
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(
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
