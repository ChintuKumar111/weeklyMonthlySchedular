package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.cartStateModel
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.example.freshyzoappmodule.ui.Fragments.NewHome_Fragment
import com.example.freshyzoappmodule.ui.Fragments.ProductSectionFragment
import com.getkeepsafe.taptargetview.TapTargetSequence
import kotlin.concurrent.thread

class NewHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewHomeBinding
    private lateinit var cartRepository: CartRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(this)

        if (savedInstanceState == null) {
            replaceFragment(NewHome_Fragment())
        }

        // Initialize shared cart preview
        loadCartState()

        // Disable icon tinting to show original colors
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(NewHome_Fragment())
                    true
                }
                R.id.nav_product -> {
                    replaceFragment(ProductSectionFragment())
                    true
                }
                R.id.nav_wallet -> {
                    startActivity(Intent(this, ChatListActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.cartPreview.setOnViewCartClickListener {
            // Handle view cart click
        }

        showHomeTour()
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
            
            // Update product quantities map
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
            .targets(
                // Your TapTarget views here
            )
            .continueOnCancel(true)
            .start()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
