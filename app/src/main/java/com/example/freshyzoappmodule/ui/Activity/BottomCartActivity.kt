package com.example.freshyzoappmodule.ui.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.ui.cartpreview.freetrial.FreeTrialBottomSheet
import com.example.freshyzoappmodule.databinding.ActivityBottomCartActivtyBinding

class BottomCartActivity : AppCompatActivity() {

    lateinit var binding: ActivityBottomCartActivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBottomCartActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()

        binding.btnClose.setOnClickListener {
           hideCartBar()
        }

        binding.tvAddToCart.setOnClickListener {

            showCartBar()
            updateCartPreview(
                itemCount = 1,
                totalPrice = 300.0
            )

           // Toast.makeText(this,"Added to cart", Toast.LENGTH_SHORT).show()

        }

        binding.cartPreviewLayout.btnViewCart.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))

        }

        binding.animationFreeTrial.setOnClickListener {
            val bottomSheet = FreeTrialBottomSheet()
            bottomSheet.show(supportFragmentManager, "FreeTrialBottomSheet")
        }
    }

    private fun showCartBar() {

        binding.floatingCartBar.apply {

            visibility = View.VISIBLE

            alpha = 0f
            scaleX = 0.95f
            scaleY = 0.95f

            post {

                translationY = height.toFloat()

                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(350)
                    .setInterpolator(
                        DecelerateInterpolator()
                    )
                    .start()
            }
        }
    }






    fun updateCartPreview(itemCount: Int, totalPrice: Double) {

        if (itemCount == 0) {
            binding.floatingCartBar.visibility = View.GONE
            return
        }

        binding.cartPreviewLayout.tvAddedItemCount.text =
            "$itemCount items"

        binding.cartPreviewLayout.tvItemAddedPrice.text =
            "₹$totalPrice"
    }




    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun hideCartBar() {
        binding.floatingCartBar.animate()
            .translationY(binding.floatingCartBar.height.toFloat())
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                binding.floatingCartBar.visibility = View.GONE
            }
            .start()
    }

}