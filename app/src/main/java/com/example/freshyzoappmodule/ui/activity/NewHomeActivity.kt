package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.cartStateModel
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.example.freshyzoappmodule.ui.Fragments.WalletFragment
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlin.concurrent.thread
import com.example.freshyzoappmodule.extensions.sizes

class NewHomeActivity : AppCompatActivity() , PaymentResultListener {

    private lateinit var binding: ActivityNewHomeBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var navController: NavController
    
    private var cachedCartState: cartStateModel? = null
    private var isFromSearch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)
        cachedCartState = cartRepository.getCartState()

        Checkout.preload(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.fragmentContainer.id) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_cart, R.id.nav_account -> {
                    binding.cartPreview.hideCart()
                }
                else -> {
                    val state = cachedCartState
                    if (state != null && state.itemsCount > 0) {
                        binding.cartPreview.showCart(state)
                    } else {
                        binding.cartPreview.hideCart()
                    }
                }
            }
        }
        
        binding.cartPreview.setOnViewCartClickListener {
            binding.bottomNavigation.selectedItemId = R.id.nav_cart
        }
        
        val initialState = cachedCartState
        if (initialState != null && initialState.itemsCount > 0) {
            binding.cartPreview.showCart(initialState)
        }

        handleIntent(intent)

        // Handle Back Press to return to SearchActivity if needed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFromSearch && navController.currentDestination?.id == R.id.nav_cart) {
                    finish() // Close NewHomeActivity and go back to SearchActivity
                } else {
                    isEnabled = false // Disable this callback
                    onBackPressedDispatcher.onBackPressed() // Perform default back action
                    isEnabled = true // Re-enable for next time
                }
            }
        })
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

    fun updateSharedCart(product: Product, priceDelta: Double, countDelta: Int, onComplete: ((cartStateModel) -> Unit)? = null) {
        thread {
            val currentState = cachedCartState ?: cartRepository.getCartState() ?: cartStateModel()
            
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
            
            // Calculate Total Price and Total Discount
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
            
            val newState = cartStateModel(
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
                val currentId = try { navController.currentDestination?.id } catch (e: Exception) { null }
                if (newState.itemsCount > 0 && currentId != R.id.nav_cart && currentId != R.id.nav_account) {
                    binding.cartPreview.showCart(newState)
                } else {
                    binding.cartPreview.hideCart()
                }
                onComplete?.invoke(newState)
            }
        }
    }

    fun getCartState(): cartStateModel {
        return cachedCartState ?: cartRepository.getCartState() ?: cartStateModel()
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
