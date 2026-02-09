package com.example.freshyzoappmodule.utils.helper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.freshyzoappmodule.databinding.BottomSheetCartPreviewBinding
import com.example.freshyzoappmodule.data.model.CartStateModel

class CartPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding =
        BottomSheetCartPreviewBinding.inflate(LayoutInflater.from(context), this, true)

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
        visibility = GONE
    }

    private fun animateIn() {

        visibility = VISIBLE

        post {
            translationY = height.toFloat()

            animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }
}