package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.databinding.ActivityProductDetailsBinding
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.sizeLabel
import com.example.freshyzoappmodule.viewmodel.ProductDetailsViewModel

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Product>("product")
        }

        if (product != null) {
            viewModel.setProduct(product)
        }

//        binding.btnSubscribe.setOnClickListener {
//            startActivity(Intent(this, DailyWeeklyMonthlySubscriptionActivity::class.java))
//        }

        viewModel.product.observe(this) { productObj ->
            productObj?.let {
                binding.tvProductName.text = it.productName
               // binding.chipVolume.text = it.sizeLabel
                binding.tvDescription.text = it.description
                binding.tvMrp.text = "₹${it.productPrice}"

//                Glide.with(this)
//                    .load(it.imageUrl)
//                    .into(binding.vpProductImages)
            }
        }
    }
}
