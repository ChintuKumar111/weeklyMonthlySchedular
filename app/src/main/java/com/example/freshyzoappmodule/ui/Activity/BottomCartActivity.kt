package com.example.freshyzoappmodule.ui.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freshyzoappmodule.data.model.CartStateModel
import com.example.freshyzoappmodule.databinding.ActivityBottomCartActivtyBinding
import com.example.freshyzoappmodule.ui.cartpreview.freetrial.FreeTrialBottomSheet
import com.example.freshyzoappmodule.viewmodel.CartViewModel

class BottomCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomCartActivtyBinding
    // ViewModel injection using the 'by viewModels()' delegate
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBottomCartActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupClicks()
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe a single state object for consistency
        viewModel.cartState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: CartStateModel) {
        // 1. Update Visibility
        if (state.isVisible) showCartBar() else hideCartBar()

        // 2. Update Data
        binding.cartPreviewLayout.tvAddedItemCount.text = "${state.itemsCount} items"
        binding.cartPreviewLayout.tvItemAddedPrice.text = "₹${state.totalPrice}"
    }

    private fun setupClicks() {
        // Notify ViewModel of the event, don't handle logic here
        binding.tvAddToCart.setOnClickListener {
            viewModel.addItemToCart(300.0)
        }

        binding.btnClose.setOnClickListener {
            viewModel.hideCart()
        }

        binding.cartPreviewLayout.btnViewCart.setOnClickListener {
            // Navigation is handled by the Activity
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.animationFreeTrial.setOnClickListener {
            FreeTrialBottomSheet().show(supportFragmentManager, "FreeTrialBottomSheet")
        }
    }

    // Animation logic stays in Activity because it's UI-related
    private fun showCartBar() {
        if (binding.floatingCartBar.visibility == View.VISIBLE) return
        binding.floatingCartBar.apply {
            visibility = View.VISIBLE
            alpha = 0f
            post {
                translationY = height.toFloat()
                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(350)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun hideCartBar() {
        if (binding.floatingCartBar.visibility == View.GONE) return
        binding.floatingCartBar.animate()
            .translationY(binding.floatingCartBar.height.toFloat())
            .alpha(0f)
            .setDuration(250)
            .withEndAction { binding.floatingCartBar.visibility = View.GONE }
            .start()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}