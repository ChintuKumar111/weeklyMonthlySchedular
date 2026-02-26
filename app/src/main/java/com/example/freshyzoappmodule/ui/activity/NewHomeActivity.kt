package com.example.freshyzoappmodule.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.freshyzoappmodule.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)

        // Preload Razorpay here for better performance
        Checkout.preload(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        // Initial load
        loadCartState()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_cart, R.id.nav_account -> {
                    // Hide cart preview on Cart and Account fragments
                    binding.cartPreview.visibility = View.GONE
                }
                else -> {
                    // Show cart preview only if it has items
                    val cartState = cartRepository.getCartState()
                    if (cartState != null && cartState.itemsCount > 0) {
                        binding.cartPreview.visibility = View.VISIBLE
                        binding.cartPreview.showCart(cartState)
                    } else {
                        binding.cartPreview.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun loadCartState() {
        thread {
            val savedCartState = cartRepository.getCartState()
            runOnUiThread {
                val currentId = try { navController.currentDestination?.id } catch (e: Exception) { null }
                if (savedCartState != null && savedCartState.itemsCount > 0 && 
                    currentId != R.id.nav_cart && currentId != R.id.nav_account) {
                    binding.cartPreview.showCart(savedCartState)
                    binding.cartPreview.visibility = View.VISIBLE
                } else {
                    binding.cartPreview.visibility = View.GONE
                }
            }
        }
    }

    fun updateSharedCart(productId: Int, priceDelta: Double, countDelta: Int) {
        thread {
            val currentState = cartRepository.getCartState() ?: cartStateModel()
            
            val newCount = currentState.itemsCount + countDelta
            val newPrice = currentState.totalPrice + priceDelta
            
            val newQuantities = currentState.productQuantities.toMutableMap()
            val currentQty = newQuantities[productId] ?: 0
            val newQty = currentQty + countDelta
            
            if (newQty <= 0) {
                newQuantities.remove(productId)
            } else {
                newQuantities[productId] = newQty
            }
            
            val newState = cartStateModel(newCount, newPrice, true, newQuantities)
            cartRepository.saveCartState(newState)
            
            runOnUiThread {
                val currentId = try { navController.currentDestination?.id } catch (e: Exception) { null }
                if (newState.itemsCount > 0 && currentId != R.id.nav_cart && currentId != R.id.nav_account) {
                    binding.cartPreview.showCart(newState)
                    binding.cartPreview.visibility = View.VISIBLE
                } else {
                    binding.cartPreview.visibility = View.GONE
                }
            }
        }
    }

    fun getCartState(): cartStateModel {
        return cartRepository.getCartState() ?: cartStateModel()
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
