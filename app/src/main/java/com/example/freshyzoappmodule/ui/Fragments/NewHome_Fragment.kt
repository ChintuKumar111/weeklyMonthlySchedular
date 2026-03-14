package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.SliderItem
import com.example.freshyzoappmodule.databinding.FragmentNewHomeBinding
import com.example.freshyzoappmodule.ui.widget.AppGuideManager
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity
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

    // For ViewPager Auto-scroll
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (_binding != null) {
            val nextItem = (binding.productSliderCart.currentItem + 1) % (binding.productSliderCart.adapter?.itemCount ?: 1)
            binding.productSliderCart.setCurrentItem(nextItem, true)
        }
    }

    private val defaultImages = listOf(
        SliderItem(0, "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w"),
        SliderItem(1, "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png"),
        SliderItem(2, "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentNewHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSlider()
        setupRecyclerViews()
        observeViewModel()
        setupCategoryClicks()

        // Wait for Activity guide to complete before showing Fragment guide
        (activity as? NewHomeActivity)?.onActivityGuideComplete = {
            if (isAdded && _binding != null) {
                showFragmentGuide()
            }
        }

        view.postDelayed({
            if (isAdded && _binding != null) {
                viewModel.fetchSlider()
                viewModel.fetchComboOffers()
                viewModel.fetchBlogReports()
            }
        }, 300)

        binding.iconNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        binding.root.findViewById<View>(R.id.btnTestReport)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_testReportFragment)
        }
    }

    private fun setupSlider() {
        binding.productSliderCart.adapter = ImageSliderAdapter(defaultImages)
        TabLayoutMediator(binding.tabLayout, binding.productSliderCart) { _, _ -> }.attach()
        binding.productSliderCart.offscreenPageLimit = 1

        binding.productSliderCart.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000) // 3 seconds delay
            }
        })
    }

    private fun setupRecyclerViews() {
        comboAdapter = ComboOfferAdapter(emptyList()) { combo ->
            Toast.makeText(requireContext(), "Added ${combo.title} to cart", Toast.LENGTH_SHORT).show()
        }
        binding.rvComboOffers.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = comboAdapter
        }

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

    private fun setupCategoryClicks() {
        binding.allProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", -1) }
            findNavController().navigate(R.id.nav_product, bundle)
        }

        binding.milkProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 2) }
            findNavController().navigate(R.id.nav_product, bundle)
        }

        binding.milkCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 1) }
            findNavController().navigate(R.id.nav_product, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showFragmentGuide() {
        val guideManager = AppGuideManager(requireActivity())
        val items = listOf(
            AppGuideManager.GuideItem(binding.iconNotification, "Notifications", "Stay updated with latest alerts.",20),
            AppGuideManager.GuideItem(binding.imgWallet, "Wallet", "You can available balance here.",20),
            AppGuideManager.GuideItem(binding.offerSection,
                "Offers",
                "Check out exclusive deals for you!",
                150),
            AppGuideManager.GuideItem(binding.rvComboOffers, "Combo Offers", "Save more with our bundled products.", 150),
            AppGuideManager.GuideItem(binding.suggestionCard, "Send Suggestion", "You can send suggestions here.", 160),
            AppGuideManager.GuideItem(binding.tvSend, "Send Suggestion", "You can send suggestions here.", 30)
        )

        guideManager.startGuide("home_fragment_guide", items)
    }

}
