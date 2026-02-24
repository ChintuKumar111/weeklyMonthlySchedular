package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.databinding.ActivityProductDetailsBinding
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.ui.activity.comparison.ComparisonBinder
import com.example.freshyzoappmodule.ui.activity.comparison.ComparisonData
import com.example.freshyzoappmodule.viewmodel.ProductDetailsViewModel
import java.io.File
import java.io.FileOutputStream

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var product: Product
    private lateinit var binding: ActivityProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize product from intent
        val intentProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Product>("product")
        }

        if (intentProduct != null) {
            this.product = intentProduct
            viewModel.setProduct(intentProduct)
            displayProductData(intentProduct)
        } else {
            // Handle cases where product is missing - maybe finish the activity
            finish()
            return
        }

        viewModel.qty.observe(this) { quantity ->
            binding.tvQty.text = quantity.toString()
        }

        binding.btnPlus.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnMinus.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        // Setup back button
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubscribe.setOnClickListener {
            val intent = Intent (this@ProductDetailsActivity, ProductSubscribeActivity::class.java)
            intent.putExtra("product",product)
            intent.putExtra("quantity", viewModel.qty.value)
            startActivity(intent)
        }

        binding.btnShare.setOnClickListener {
            shareProduct()
        }
        // comparison section
        val comparisonBinder = ComparisonBinder(binding)
        comparisonBinder.bind(ComparisonData.getMilkComparison())

        viewModel.product.observe(this) { productObj ->
            productObj?.let {
                this.product = it
                displayProductData(it)
            }
        }
    }

    private fun shareProduct() {
        val productName = product.productName
        val price = product.productPrice
        val description = product.shortDesc
        val appLink = "https://play.google.com/store/search?q=freshyzo&c=apps&hl=en_IN}"
        
        val shareText = """
            🛒 Check out this product on Freshyzo!
            
            Product: $productName
            Price: ₹$price
       
            $description
            
            Download Freshyzo App Now: $appLink
        """.trimIndent()

        Glide.with(this)
            .asBitmap()
            .load(product.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareImageAndText(resource, shareText)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
                
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // If image load fails, share text only
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, shareText)
                    startActivity(Intent.createChooser(intent, "Share via"))
                }
            })
    }


    private fun displayProductData(product: Product) {
        val sizes = product.sizes
        binding.tvProductName.text = product.productName
        binding.tvDescription.text = product.description
        binding.tvSellingPrice.text = "₹${product.productPrice}"
        binding.tvVolume.text = "${product.unit}"
        binding.tvVolume.text = sizes.getOrNull(0)?.label ?: ""
        binding.tvVolume.isSelected = true
        binding.tvMrp.apply { text = "₹${product.dairyMrp}"
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        // Calculate discount percentage
        val price = product.productPrice.toDoubleOrNull() ?: 0.0
        val mrp = product.dairyMrp.toDoubleOrNull() ?: 0.0
        if (mrp > price) {
            val discount = ((mrp - price) / mrp * 100).toInt()
            binding.tvDiscount.text = "$discount% OFF"
            binding.tvDiscount.visibility = View.VISIBLE
        } else {
            binding.tvDiscount.visibility = View.GONE
        }

        Glide.with(this)
            .load(product.imageUrl)
            .into(binding.ivProductImage)
    }

    private fun shareImageAndText(bitmap: Bitmap, text: String) {
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/product_image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imageFile = File(cachePath, "product_image.png")
            val contentUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)

            if (contentUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                shareIntent.type = "image/png"
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
