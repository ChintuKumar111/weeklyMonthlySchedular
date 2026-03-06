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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.categoryModel
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding
import com.example.freshyzoappmodule.extensions.categoryId
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity
import com.example.freshyzoappmodule.ui.activity.ProductDetailsActivity
import com.example.freshyzoappmodule.ui.activity.ProductSubscribeActivity
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
    private var isScrollingFromCategory = false
    private var pendingCategoryId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get category ID from navigation arguments
        pendingCategoryId = arguments?.getInt("category_id", -1) ?: -1

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
            findNavController().navigate(R.id.nav_home)
        }
    }

    override fun onResume() {
        super.onResume()
        // Sync quantities when returning to the fragment
        syncCartQuantities()
    }

    private fun syncCartQuantities() {
        if (::productAdapter.isInitialized) {
            val sharedQuantities = (activity as? NewHomeActivity)?.getCartState()?.productQuantities ?: emptyMap()
            productAdapter.setInitialQuantities(sharedQuantities)
        }
    }
    private fun setupCategories() {
        val categories = listOf(
            categoryModel(1, "Milk", R.drawable.milk_),
            categoryModel(2, "Ghee", R.drawable.ghee),
            categoryModel(3, "Dahi", R.drawable.dahi),
            categoryModel(4, "Paneer", R.drawable.paneer),
            categoryModel(6, "Khowa", R.drawable.khowa),
        )
        categoryAdapter = CategoryAdapter(categories) { category, _ ->
            scrollToCategory(category.id)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    private fun scrollToCategory(categoryId: Int) {
        val position = allProducts.indexOfFirst { it.categoryId == categoryId }
        if (position != -1) {
            isScrollingFromCategory = true
            (binding.rvProducts.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(position, 0)
            
            // Update category selection in the vertical list
            val categoryPos = categoryAdapter.getPositionForId(categoryId)
            if (categoryPos != -1) {
                categoryAdapter.updateSelection(categoryPos)
                binding.rvCategories.smoothScrollToPosition(categoryPos)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.productList.observe(viewLifecycleOwner, Observer { products ->
            allProducts = products.sortedBy { it.categoryId }
            syncCartQuantities()
            productAdapter.submitList(allProducts)

            // Auto-scroll if a category was passed via navigation
            if (pendingCategoryId != -1) {
                // Delay slightly to ensure RecyclerView has laid out its items
                binding.rvProducts.post {
                    scrollToCategory(pendingCategoryId)
                    pendingCategoryId = -1 // Reset after scrolling
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

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(
            onAddClick = { product, size, qty ->
                (activity as? NewHomeActivity)?.updateSharedCart(product, size.price.toDouble() * qty, qty)
            },
            onQtyChange = { product, size, delta ->
                (activity as? NewHomeActivity)?.updateSharedCart(product, size.price.toDouble() * delta, delta)
            },
            onSubscribeClick = { product ->
                val intent = Intent(requireContext(), ProductSubscribeActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onProductClick = { product ->
                val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )

        binding.rvProducts.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = productAdapter
            
            setHasFixedSize(true)
            itemAnimator = null
            recycledViewPool.setMaxRecycledViews(0, 20)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var lastCategoryId = -1
                
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isScrollingFromCategory = false
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isScrollingFromCategory) {
                        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition != RecyclerView.NO_POSITION && allProducts.isNotEmpty()) {
                            val categoryId = allProducts[firstVisibleItemPosition].categoryId
                            
                            if (categoryId != lastCategoryId) {
                                lastCategoryId = categoryId
                                val categoryPosition = categoryAdapter.getPositionForId(categoryId)
                                if (categoryPosition != -1) {
                                    categoryAdapter.updateSelection(categoryPosition)
                                    binding.rvCategories.smoothScrollToPosition(categoryPosition)
                                }
                            }
                        }
                    }
                }
            })
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
