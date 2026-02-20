package com.example.freshyzoappmodule.helper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.freshyzoappmodule.data.model.CartStateModel
import com.example.freshyzoappmodule.databinding.BottomSheetCartPreviewBinding

class CartPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding =
        BottomSheetCartPreviewBinding.inflate(
            LayoutInflater.from(context), 
            this,
            true)

    private var viewCartClick: (() -> Unit)? = null

    init {
        visibility = GONE

        binding.btnViewCart.setOnClickListener {
            viewCartClick?.invoke()
        }
    }

    fun setOnViewCartClickListener(listener: () -> Unit) {
        viewCartClick = listener
    }

    fun showCart(cartData: CartStateModel) {

        // 🔥 Auto collapse when empty
        if (cartData.itemsCount <= 0) {
            hideCart()
            return
        }

        binding.tvAddedItemCount.text =
            "${cartData.itemsCount} items"

        binding.tvItemAddedPrice.text =
            "₹${cartData.totalPrice}"

        animateIn()
    }

    fun hideCart() {
        if (visibility == GONE) return
        
        animate()
            .translationY(height.toFloat())
            .setDuration(300)
            .withEndAction { 
                visibility = GONE 
            }
            .start()
    }

    private fun animateIn() {
        if (visibility == VISIBLE && translationY == 0f) {
            return
        }

        // To avoid "blink", we ensure the view is off-screen before making it visible
        if (height == 0) {
            // If height isn't measured yet (first time), wait for layout
            visibility = INVISIBLE
            post {
                if (height > 0) {
                    translationY = height.toFloat()
                    visibility = VISIBLE
                    animate()
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                }
            }
        } else {
            translationY = height.toFloat()
            visibility = VISIBLE
            animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }
}
