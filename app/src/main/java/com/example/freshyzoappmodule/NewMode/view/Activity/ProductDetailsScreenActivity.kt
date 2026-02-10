package com.example.freshyzoappmodule.NewMode.view.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.example.freshyzoappmodule.NewMode.ViewModel.ProductViewModel
import com.example.freshyzoappmodule.NewMode.view.adapter.ImageSliderAdapter
import com.example.freshyzoappmodule.databinding.ActivityProductDetailsScreenBinding

class ProductDetailsScreenActivity : AppCompatActivity() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var binding: ActivityProductDetailsScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.getIntExtra("id", 0)

        viewModel.loadProductDetail(id)

        viewModel.productDetail.observe(this) { productResponse ->
            productResponse?.let { product ->
                // 1. Setup Image Slider
                val adapter = ImageSliderAdapter(product.images)
                binding.imageSlider.adapter = adapter

                // 2. Update Text Views (Ensure these IDs exist in your XML)
                binding.tvTitle.text = product.title
                binding.tvPrice.text = "₹${product.price}"
                binding.tvDescription.text = product.description
            }
        }

    }
}