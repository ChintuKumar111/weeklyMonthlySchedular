package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.SliderItem
import com.example.freshyzoappmodule.databinding.FragmentNewHomeBinding
import com.example.freshyzoappmodule.ui.activity.NotificationActivity
import com.example.freshyzoappmodule.ui.adapter.BlogReportAdapter
import com.example.freshyzoappmodule.ui.adapter.ComboOfferAdapter
import com.example.freshyzoappmodule.ui.adapter.ImageSliderAdapter
import com.example.freshyzoappmodule.viewmodel.HomeFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator

class NewHome_Fragment : Fragment() {
    private var _binding: FragmentNewHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeFragmentViewModel by viewModels()
    
    private lateinit var comboAdapter: ComboOfferAdapter
    private lateinit var blogAdapter: BlogReportAdapter

    private val defaultImages = listOf(
        SliderItem(0, "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w"),
        SliderItem(1, "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png"),
        SliderItem(2, "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSlider()
        setupRecyclerViews()
        observeViewModel()
        setupCategoryClicks()
        
        // Fetch data
        view.postDelayed({
            if (isAdded) {
                viewModel.fetchSlider()
                viewModel.fetchComboOffers()
                viewModel.fetchBlogReports()
            }
        }, 300)

        binding.iconNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    private fun setupCategoryClicks() {
        binding.allProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", -1) }
            findNavController().navigate(R.id.nav_product, bundle)
        }

        binding.milkProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 2) } // 2 is Ghee
            findNavController().navigate(R.id.nav_product, bundle)
        }

        binding.milkCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 1) } // 1 is Milk
            findNavController().navigate(R.id.nav_product, bundle)
        }
    }

    private fun setupSlider() {
        binding.productSliderCart.adapter = ImageSliderAdapter(defaultImages)
        TabLayoutMediator(binding.tabLayout, binding.productSliderCart) { _, _ -> }.attach()
        binding.productSliderCart.offscreenPageLimit = 1
    }

    private fun setupRecyclerViews() {
        // Combo Offers
        comboAdapter = ComboOfferAdapter(emptyList()) { combo ->
            Toast.makeText(requireContext(), "Added ${combo.title} to cart", Toast.LENGTH_SHORT).show()
        }
        binding.rvComboOffers.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = comboAdapter
        }

        // Blog Reports
        blogAdapter = BlogReportAdapter(emptyList()) { blog ->
            Toast.makeText(requireContext(), "Opening ${blog.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvBlogReports.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = blogAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.sliderData.observe(viewLifecycleOwner) { sliderList ->
            if (!sliderList.isNullOrEmpty()) {
                binding.productSliderCart.adapter = ImageSliderAdapter(sliderList)
            }
        }

        viewModel.comboOffers.observe(viewLifecycleOwner) { combos ->
            comboAdapter.updateList(combos)
        }

        viewModel.blogReports.observe(viewLifecycleOwner) { blogs ->
            blogAdapter.updateList(blogs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
