package com.example.freshyzoappmodule.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.response.Banner
import com.example.freshyzoappmodule.data.manager.AppGuideManager
import com.example.freshyzoappmodule.data.model.HomeProductDeliveryCalendar
import com.example.freshyzoappmodule.databinding.FragmentHomeBinding
import com.example.freshyzoappmodule.helper.generateDates
import com.example.freshyzoappmodule.ui.activity.HomeActivity
import com.example.freshyzoappmodule.ui.activity.NotificationActivity
import com.example.freshyzoappmodule.ui.adapter.HomeBlogAdapter
import com.example.freshyzoappmodule.ui.adapter.CalendarAdapter
import com.example.freshyzoappmodule.ui.adapter.HomeComboOfferAdapter
import com.example.freshyzoappmodule.ui.adapter.CalendarProductDeliveryDetailsAdapter
import com.example.freshyzoappmodule.ui.adapter.ImageSliderAdapter
import com.example.freshyzoappmodule.ui.widget.PermissionManager
import com.example.freshyzoappmodule.ui.viewmodel.HomeFragmentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class Home_Fragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by viewModel()
    val dateList = generateDates()
    private lateinit var comboAdapter: HomeComboOfferAdapter
    private lateinit var blogAdapter: HomeBlogAdapter
    private lateinit var permissionManager: PermissionManager

    private lateinit var calendarAdapter: CalendarAdapter
    private var deliveryDialog: Dialog? = null

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

        // Wait for Activity UI to be ready, then Fragment guide items can be accessed
        (activity as? HomeActivity)?.onActivityGuideComplete = {
            if (isAdded && _binding != null) {
                // Now Activity knows Home Fragment is ready to provide tour views
            }
        }

        // Start fetching data immediately instead of waiting 300ms
        viewModel.fetchSlider()
        viewModel.fetchComboOffers()
        viewModel.fetchBlogReports()

        if (!permissionManager.isNotificationPermissionGranted()) {
            permissionManager.askNotificationPermission()
        }

        binding.iconNotification.setOnClickListener {
            if (permissionManager.isNotificationPermissionGranted()) {
                startActivity(Intent(requireContext(), NotificationActivity::class.java))
            } else {
                permissionManager.askNotificationPermission()
            }
        }

        // Use the activity's helper to ensure Bottom Navigation syncs correctly
        binding.llWallet.setOnClickListener {
            (activity as? HomeActivity)?.navigateToTab(R.id.nav_wallet)
        }

        binding.root.findViewById<View>(R.id.testReportCard)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_testReportFragment)
        }
        binding.root.findViewById<View>(R.id.btnTestReport)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_testReportFragment)
        }

    }

    fun getTourItems(): List<AppGuideManager.GuideItem> {
        return listOf(
            AppGuideManager.GuideItem(binding.iconNotification, "Notifications", "Stay updated with latest alerts.", 30, false),
            AppGuideManager.GuideItem(binding.imgWallet, "Wallet", "Check your available balance here.", 30, false)
        )
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
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    private fun setupRecyclerViews() {
        comboAdapter = HomeComboOfferAdapter(emptyList()) { combo ->
            Toast.makeText(requireContext(), "Added ${combo.title} to cart", Toast.LENGTH_SHORT).show()
        }
        binding.rvComboOffers.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = comboAdapter
        }
        blogAdapter = HomeBlogAdapter(emptyList()) { blog ->
            val bundle = Bundle().apply {
                putParcelable("blog", blog)
            }
            findNavController().navigate(R.id.action_nav_home_to_blogReportFragment, bundle)
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
        viewModel.homeComboOffers.observe(viewLifecycleOwner) { combos ->
            comboAdapter.updateList(combos)
        }
        viewModel.homeBlogReports.observe(viewLifecycleOwner) { blogs ->
            blogAdapter.updateList(blogs)
        }
        viewModel.deliveryProducts.observe(viewLifecycleOwner) { products ->
            updateDeliveryDialog(products ?: emptyList())
        }
    }

    private fun setupCategoryClicks() {
        binding.allProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", -1) }
            (activity as? HomeActivity)?.navigateToTab(R.id.nav_product, bundle)
        }
        binding.milkProductCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 2) }
            (activity as? HomeActivity)?.navigateToTab(R.id.nav_product, bundle)
        }
        binding.milkCard.setOnClickListener {
            val bundle = Bundle().apply { putInt("category_id", 1) }
            (activity as? HomeActivity)?.navigateToTab(R.id.nav_product, bundle)
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
            showDeliveryDetailsDialog(null)
            viewModel.getDeliveryProducts(selectedDay.fullDate)
        }
        binding.rvCalendar.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendar.adapter = calendarAdapter
    }

    private fun showDeliveryDetailsDialog(products: List<HomeProductDeliveryCalendar>?) {
        deliveryDialog = Dialog(requireContext())
        deliveryDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deliveryDialog?.setContentView(R.layout.dialog_delivery_details)
        
        deliveryDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deliveryDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val pbLoading = deliveryDialog?.findViewById<ProgressBar>(R.id.pbLoading)
        val btnOk = deliveryDialog?.findViewById<Button>(R.id.btnOk)

        if (products == null) {
            pbLoading?.visibility = View.VISIBLE
        } else {
            updateDeliveryDialog(products)
        }

        btnOk?.setOnClickListener {
            deliveryDialog?.dismiss()
        }

        deliveryDialog?.show()
    }

    private fun updateDeliveryDialog(products: List<HomeProductDeliveryCalendar>) {
        val dialog = deliveryDialog ?: return
        if (!dialog.isShowing) return

        val pbLoading = dialog.findViewById<ProgressBar>(R.id.pbLoading)
        val tvNoDelivery = dialog.findViewById<TextView>(R.id.tvNoDelivery)
        val rvDeliveryProducts = dialog.findViewById<RecyclerView>(R.id.rvDeliveryProducts)

        pbLoading?.visibility = View.GONE

        if (products.isEmpty()) {
            tvNoDelivery?.visibility = View.VISIBLE
            rvDeliveryProducts?.visibility = View.GONE
        } else {
            tvNoDelivery?.visibility = View.GONE
            rvDeliveryProducts?.visibility = View.VISIBLE
            rvDeliveryProducts?.layoutManager = LinearLayoutManager(requireContext())
            rvDeliveryProducts?.adapter = CalendarProductDeliveryDetailsAdapter(products)
        }
    }
}
