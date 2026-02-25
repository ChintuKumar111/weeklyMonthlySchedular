package com.example.freshyzoappmodule.ui.activity

import android.os.Bundle
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

        // showHomeTour() // Commented out to prevent empty sequence hang
        loadCartState()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null
   }

    private fun loadCartState() {
        thread {
            val savedCartState = cartRepository.getCartState()
            runOnUiThread {
                if (savedCartState != null && savedCartState.itemsCount > 0) {
                    binding.cartPreview.showCart(savedCartState)
                } else {
                    binding.cartPreview.hideCart()
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
                if (newState.itemsCount > 0) {
                    binding.cartPreview.showCart(newState)
                } else {
                    binding.cartPreview.hideCart()
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
