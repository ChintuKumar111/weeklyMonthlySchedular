package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.cartStateModel
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.getkeepsafe.taptargetview.TapTargetSequence
import kotlin.concurrent.thread

class NewHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewHomeBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)


        showHomeTour()
        // Initialize shared cart preview
        loadCartState()


        // Setup Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        // Link BottomNavigationView with NavController

        binding.bottomNavigation.setupWithNavController(navController)
        // Disable icon tinting to show original colors
        binding.bottomNavigation.itemIconTintList = null

//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            if (item.itemId == R.id.nav_wallet) {
//                startActivity(Intent(this, ChatListActivity::class.java))
//                false
//            } else {
//                NavigationUI.onNavDestinationSelected(item, navController)
//            }
//        }
//
//        binding.cartPreview.setOnViewCartClickListener {
//            // Handle view cart click
//        }
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

    private fun showHomeTour() {
        TapTargetSequence(this)
            .targets()
            .continueOnCancel(true)
            .start()
    }
}
