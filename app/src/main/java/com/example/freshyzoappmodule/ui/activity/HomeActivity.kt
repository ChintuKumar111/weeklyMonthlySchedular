package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.CartState
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ActivityHomeBinding
import com.example.freshyzoappmodule.ui.fragments.WalletFragment
import com.example.freshyzoappmodule.ui.fragments.Home_Fragment
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlin.concurrent.thread
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.helper.BaseActivityy
import com.example.freshyzoappmodule.data.manager.AppGuideManager

class HomeActivity : BaseActivityy() , PaymentResultListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var navController: NavController
    
    private var cachedCartState: CartState? = null
    private var isFromSearch: Boolean = false

    // Signal for Fragment guide readiness
    var onActivityGuideComplete: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)
        cachedCartState = cartRepository.getCartState()

        Checkout.preload(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.fragmentContainer.id) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        // Initialize visibility logic
        setupNavigationVisibility()
        
        binding.cartPreview.setOnViewCartClickListener {
            binding.bottomNavigation.selectedItemId = R.id.nav_cart
        }
        handleIntent(intent)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFromSearch && navController.currentDestination?.id == R.id.nav_cart) {
                    finish()
                } else {
                    if (!navController.popBackStack()) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
        
        // Signal that the activity UI is ready
        binding.root.post { onActivityGuideComplete?.invoke() }

        // Start the Main App Tour with a small delay to ensure fragments are loaded
        binding.root.postDelayed({
            showMainAppTour()
        }, 1500)
    }
    private fun showMainAppTour() {
        val guideManager = AppGuideManager(this)
        val prefKey = "main_app_tour_v10" // Fresh key to ensure it shows

        guideManager.showWelcomeDialog(prefKey, 
            onStart = {
                // 1. Get items from Home Fragment (Notifications, Top Wallet)
                val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as? NavHostFragment
                val homeFragment = navHostFragment?.childFragmentManager?.fragments?.find { it is Home_Fragment } as? Home_Fragment
                val homeItems = homeFragment?.getTourItems() ?: emptyList()

                // 2. Define Bottom Navigation items
                val homeTab = binding.bottomNavigation.findViewById<View>(R.id.nav_home)
                val productTab = binding.bottomNavigation.findViewById<View>(R.id.nav_product)
                val walletTab = binding.bottomNavigation.findViewById<View>(R.id.nav_wallet)
                val accountTab = binding.bottomNavigation.findViewById<View>(R.id.nav_account)
                val cartTab = binding.bottomNavigation.findViewById<View>(R.id.nav_cart)

                val bottomNavItems = listOf(
                    AppGuideManager.GuideItem(homeTab, "Home", "Your daily dashboard.", 35, false),
                    AppGuideManager.GuideItem(productTab, "Products", "Browse all fresh dairy products.", 35, true),
                    AppGuideManager.GuideItem(walletTab, "My Wallet", "Add money and check transactions.", 35, true),
                    AppGuideManager.GuideItem(accountTab, "My Account", "Manage profile, address and orders.", 35, true),
                    AppGuideManager.GuideItem(cartTab, "Checkout", "Review and confirm your purchases.", 35, true)
                )

                // 3. Combine and start sequence
                guideManager.startGuide(prefKey, homeItems + bottomNavItems)
            },
            onSkip = {
                // Tour skipped or already seen
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupNavigationVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 1. Manage Bottom Navigation Visibility with smooth transition
            val isTopLevelDestination = when (destination.id) {
                R.id.nav_home, R.id.nav_product, R.id.nav_wallet, R.id.nav_account, R.id.nav_cart -> true
                else -> false
            }
            toggleViewVisibility(binding.bottomNavigation, isTopLevelDestination)

            // 2. Manage Cart Preview Visibility
            updateCartPreviewVisibility(destination.id)
        }
    }

    private fun toggleViewVisibility(view: View, show: Boolean) {
        val translationOffset = 48f * resources.displayMetrics.density // 48dp slide offset

        if (show) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
                view.alpha = 0f
                view.translationY = translationOffset
            }
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .setListener(null)
                .start()
        } else {
            if (view.visibility == View.VISIBLE) {
                view.animate()
                    .alpha(0f)
                    .translationY(translationOffset)
                    .setDuration(400)
                    .setInterpolator(android.view.animation.AccelerateInterpolator())
                    .withEndAction {
                        view.visibility = View.GONE
                    }
                    .start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cachedCartState = cartRepository.getCartState()
        updateCartPreviewVisibility(navController.currentDestination?.id)
    }

    private fun updateCartPreviewVisibility(destinationId: Int?) {
        val state = cachedCartState
        val shouldShowCart = state != null && state.itemsCount > 0 && when (destinationId) {
            R.id.nav_cart, R.id.nav_account -> false
            else -> true
        }

        if (shouldShowCart && state != null) {
            binding.cartPreview.showCart(state)
            toggleViewVisibility(binding.cartPreview, true)
        } else {
            toggleViewVisibility(binding.cartPreview, false)
            binding.cartPreview.hideCart()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        isFromSearch = intent?.getBooleanExtra("FROM_SEARCH", false) == true
        if (intent?.getBooleanExtra("OPEN_CART", false) == true) {
            binding.bottomNavigation.selectedItemId = R.id.nav_cart
        }
    }

    fun updateSharedCart(product: Product, priceDelta: Double, countDelta: Int, onComplete: ((CartState) -> Unit)? = null) {
        thread {
            val currentState = cartRepository.getCartState() ?: CartState()
            val newCount = currentState.itemsCount + countDelta
            val productId = product.productId.toIntOrNull() ?: 0
            val newQuantities = currentState.productQuantities.toMutableMap()
            val currentQty = newQuantities[productId] ?: 0
            val newQty = currentQty + countDelta
            val currentProducts = currentState.products.toMutableList()

            if (newQty <= 0) {
                newQuantities.remove(productId)
                currentProducts.removeAll { (it.productId.toIntOrNull() ?: 0) == productId }
            } else {
                newQuantities[productId] = newQty
                if (!currentProducts.any { (it.productId.toIntOrNull() ?: 0) == productId }) {
                    currentProducts.add(product)
                }
            }

            var totalMRP = 0.0
            var totalSellingPrice = 0.0
            currentProducts.forEach { p ->
                val qty = newQuantities[p.productId.toIntOrNull() ?: 0] ?: 0
                val size = p.sizes.firstOrNull()
                if (size != null) {
                    totalMRP += size.originalPrice.toDouble() * qty
                    totalSellingPrice += size.price.toDouble() * qty
                }
            }
            val totalDiscount = totalMRP - totalSellingPrice
            val newState = CartState(
                itemsCount = if (newCount < 0) 0 else newCount, 
                totalPrice = if (totalSellingPrice < 0.0) 0.0 else totalSellingPrice, 
                isVisible = true, 
                productQuantities = newQuantities, 
                products = currentProducts,
                discount = if (totalDiscount < 0.0) 0.0 else totalDiscount
            )
            cachedCartState = newState
            cartRepository.saveCartState(newState)
            runOnUiThread {
                updateCartPreviewVisibility(navController.currentDestination?.id)
                onComplete?.invoke(newState)
            }
        }
    }

    fun getCartState(): CartState {
        return cachedCartState ?: cartRepository.getCartState() ?: CartState()
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as? NavHostFragment
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is WalletFragment) {
                fragment.onPaymentSuccess(razorpayPaymentID)
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as? NavHostFragment
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is WalletFragment) {
                fragment.onPaymentError(code, response)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Checkout.clearUserData(this)
    }
}
