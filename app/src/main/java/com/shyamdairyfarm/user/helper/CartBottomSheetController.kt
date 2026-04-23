package com.shyamdairyfarm.user.helper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.shyamdairyfarm.user.data.model.CartUiState
import com.shyamdairyfarm.user.databinding.BottomAddToCartControllerDesignBinding

class CartBottomSheetController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = BottomAddToCartControllerDesignBinding.inflate(LayoutInflater.from(context), this, true)
    private var viewCartClick: (() -> Unit)? = null

    init {
        visibility = View.GONE
        binding.btnViewCart.setOnClickListener {
            viewCartClick?.invoke()
        }
    }

    fun setOnViewCartClickListener(listener: () -> Unit) {
        viewCartClick = listener
    }

    fun showCart(cartData: CartUiState) {
        // Update labels first
        binding.tvAddedItemCount.text = "${cartData.itemsCount} items"
        binding.tvItemAddedPrice.text = "₹${cartData.totalPrice}"

        if (cartData.itemsCount <= 0) {
            hideCart()
            return
        }

        // Only trigger animation if not already visible or if it was hiding
        if (visibility != View.VISIBLE || translationY > 0f) {
            animateIn()
        }
    }

    fun hideCart() {
        if (visibility == View.GONE) return
        
        animate().cancel()
        
        // Ensure height is measured. Fallback to a large value if not.
        val targetY = if (height > 0) height.toFloat() else 600f
        
        animate()
            .translationY(targetY)
            .alpha(0f)
            .setDuration(350)
            .withEndAction { 
                visibility = View.GONE
                translationY = 0f
                alpha = 1f
            }
            .start()
    }

    private fun animateIn() {
        animate().cancel()
        alpha = 1f

        if (height == 0) {
            // Wait for measurement
            visibility = View.INVISIBLE
            post {
                if (height > 0) {
                    translationY = height.toFloat()
                    visibility = View.VISIBLE
                    animate()
                        .translationY(0f)
                        .setDuration(350)
                        .start()
                } else {
                    visibility = View.VISIBLE
                    translationY = 0f
                }
            }
        } else {
            if (visibility != View.VISIBLE) {
                translationY = height.toFloat()
                visibility = View.VISIBLE
            }
            animate()
                .translationY(0f)
                .setDuration(350)
                .start()
        }
    }
}
