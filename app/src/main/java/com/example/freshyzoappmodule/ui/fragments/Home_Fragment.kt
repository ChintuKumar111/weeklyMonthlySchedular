package com.example.freshyzoappmodule.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Banner
import com.example.freshyzoappmodule.data.manager.AppGuideManager
import com.example.freshyzoappmodule.databinding.FragmentHomeBinding
import com.example.freshyzoappmodule.helper.generateDates
import com.example.freshyzoappmodule.ui.activity.HomeActivity
import com.example.freshyzoappmodule.ui.activity.NotificationActivity
import com.example.freshyzoappmodule.ui.adapter.BlogReportAdapter
import com.example.freshyzoappmodule.ui.adapter.CalendarAdapter
import com.example.freshyzoappmodule.ui.adapter.ComboOfferAdapter
import com.example.freshyzoappmodule.ui.adapter.ImageSliderAdapter
import com.example.freshyzoappmodule.ui.widget.PermissionManager
import com.example.freshyzoappmodule.ui.viewmodel.HomeFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class Home_Fragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeFragmentViewModel by viewModel()
    val dateList = generateDates()

    private lateinit var comboAdapter: ComboOfferAdapter
    private lateinit var blogAdapter: BlogReportAdapter
    private lateinit var permissionManager: PermissionManager

    private lateinit var calendarAdapter: CalendarAdapter
    // For ViewPager Auto-scroll
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (_binding != null) {
            val nextItem = (binding.productSliderCart.currentItem + 1) % (binding.productSliderCart.adapter?.itemCount ?: 1)
            binding.productSliderCart.setCurrentItem(nextItem, true)
        }
    }

    private val defaultImages = listOf(
        Banner(0, "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w"),
        Banner(1, "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png"),
        Banner(2, "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionManager = PermissionManager(this)

        setupSlider()
        setupRecyclerViews()
        observeViewModel()
        setupCategoryClicks()
        setupRecyclerCalendar()

        // Wait for Activity guide to complete before showing Fragment guide
        (activity as? HomeActivity)?.onActivityGuideComplete = {
            if (isAdded && _binding != null) {
                showFragmentGuide()
            }
        }

        view.postDelayed({
            if (isAdded && _binding != null) {
                viewModel.fetchSlider()
                viewModel.fetchComboOffers()
                viewModel.fetchBlogReports()

                // Automatically ask for notification permission on home screen
                if (!permissionManager.isNotificationPermissionGranted()) {
                    permissionManager.askNotificationPermission()
                }
            }
        }, 300)

        binding.iconNotification.setOnClickListener {
            if (permissionManager.isNotificationPermissionGranted()) {
                // If already granted, just open the activity
                startActivity(Intent(requireContext(), NotificationActivity::class.java))
            } else {
                // If not granted, ask for permission
                permissionManager.askNotificationPermission()
            }
        }


        binding.root.findViewById<View>(R.id.testReportCard)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_testReportFragment)
        }
    }

    private fun setupSlider() {
        binding.productSliderCart.adapter = ImageSliderAdapter(defaultImages) { banner ->
            findNavController().navigate(R.id.action_nav_home_to_offerDetailsFragment)
        }
        binding.dotsIndicator.attachTo(binding.productSliderCart)
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
                binding.productSliderCart.adapter = ImageSliderAdapter(sliderList) { banner ->
                    findNavController().navigate(R.id.action_nav_home_to_offerDetailsFragment)
                }
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

    private fun setupRecyclerCalendar(){
     calendarAdapter = CalendarAdapter(dateList.toMutableList()) { selectedDay ->

            viewModel.getDeliveryProducts(selectedDay.fullDate)

        }

        binding.rvCalendar.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvCalendar.adapter = calendarAdapter

    }
    private fun showFragmentGuide() {
        val guideManager = AppGuideManager(requireActivity())
        val items = listOf(
            AppGuideManager.GuideItem(binding.iconNotification, "Notifications", "Stay updated with latest alerts.",20),
            AppGuideManager.GuideItem(binding.imgWallet, "Wallet", "You can available balance here.",20),

        )

        guideManager.startGuide("home_fragment_guide", items) {
             // onComplete: Ensure clicks work after guide
        }
    }
}
