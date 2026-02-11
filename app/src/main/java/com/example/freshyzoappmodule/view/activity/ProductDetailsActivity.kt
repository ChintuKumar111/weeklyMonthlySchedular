package com.example.freshyzoappmodule.view.Activity

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.databinding.ActivityProductDetailsBinding
import com.example.freshyzoappmodule.viewmodel.ProductDetailsViewModel

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", ProductModel::class.java)
        } else {
            intent.getParcelableExtra<ProductModel>("product")
        }

        if (product != null) {
            viewModel.setProduct(product)
        }

        viewModel.product.observe(this) { productModel ->
            binding.productTitle.text = productModel.product_name

            val nameParts = productModel.product_name.split(" ")
            if (nameParts.size >= 2 && nameParts[nameParts.size - 2].toIntOrNull() != null) {
                val weight = nameParts.takeLast(2).joinToString(" ")
                binding.productUnit.text = weight
            } else {
                binding.productUnit.text = productModel.unit
            }

            binding.productDescription.text = productModel.description
            binding.currentPrice.text = "₹${productModel.product_price}"

            Glide.with(this)
                .load("https://freshyzo.com/admin/uploads/product_image/" + productModel.dairy_product_image)
                .into(binding.productImage)
        }
    }
}
