package com.example.freshyzoappmodule.ui.activity

import android.os.Bundle
import android.view.View
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

class NewHomeActivity : AppCompatActivity() , PaymentResultListener {

    private lateinit var binding: ActivityNewHomeBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var navController: NavController
    
    // Cache the cart state to avoid repeated disk I/O and JSON parsing on the main thread
    private var cachedCartState: cartStateModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)
        // Load once at start
        cachedCartState = cartRepository.getCartState()

        // Preload Razorpay
        Checkout.preload(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_cart, R.id.nav_account -> {
                    binding.cartPreview.hideCart()
                }
                else -> {
                    // Use cached state instead of reading from disk during navigation transitions
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
        
        // Initial setup for the preview if needed
        val initialState = cachedCartState
        if (initialState != null && initialState.itemsCount > 0) {
            binding.cartPreview.showCart(initialState)
        }
    }

    fun updateSharedCart(product: Product, priceDelta: Double, countDelta: Int, onComplete: ((cartStateModel) -> Unit)? = null) {
        thread {
            val currentState = cachedCartState ?: cartRepository.getCartState() ?: cartStateModel()
            
            val newCount = currentState.itemsCount + countDelta
            val newPrice = currentState.totalPrice + priceDelta
            
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
            
            val newState = cartStateModel(
                itemsCount = if (newCount < 0) 0 else newCount, 
                totalPrice = if (newPrice < 0.0) 0.0 else newPrice, 
                isVisible = true, 
                productQuantities = newQuantities, 
                products = currentProducts
            )
            
            // Update cache and save to disk
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is WalletFragment) {
                fragment.onPaymentSuccess(razorpayPaymentID)
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
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
