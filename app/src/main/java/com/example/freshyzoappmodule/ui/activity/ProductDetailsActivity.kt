package com.example.freshyzoappmodule.ui.activity

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.objects.FaqManager
import com.example.freshyzoappmodule.databinding.ActivityProductDetailsBinding
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.ui.activity.comparison.ComparisonBinder
import com.example.freshyzoappmodule.ui.activity.comparison.ComparisonData
import com.example.freshyzoappmodule.ui.adapter.FaqAdapter
import com.example.freshyzoappmodule.viewmodel.ProductDetailsViewModel
import java.io.File
import java.io.FileOutputStream
import android.content.Intent as AndroidIntent

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var product: Product

    private val viewModel: ProductDetailsViewModel by viewModels()
    private val faqAdapter = FaqAdapter()

    // ─────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentProduct = parseProductFromIntent() ?: run { finish(); return }
        product = intentProduct
        viewModel.setProduct(intentProduct)

        setupFaq(intentProduct.productName)
        setupComparison()
        setupClickListeners()
        observeViewModel()
        displayProductData(intentProduct)
    }

    // ─────────────────────────────────────────────────────────────
    //  Setup
    // ─────────────────────────────────────────────────────────────

    private fun parseProductFromIntent(): Product? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("product")
        }

    private fun setupFaq(productName: String) {
        binding.rvCardFrag.rvFaq.apply {
            layoutManager = LinearLayoutManager(this@ProductDetailsActivity)
            adapter = faqAdapter
        }
        faqAdapter.submitList(FaqManager.getFaqList(productName))
    }

    private fun setupComparison() {
        ComparisonBinder(binding).bind(ComparisonData.getMilkComparison())
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPlus.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnMinus.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        binding.btnSubscribe.setOnClickListener {
            animateButton(binding.btnSubscribe)
            startActivity(
                AndroidIntent(this, ProductSubscribeActivity::class.java).apply {
                    putExtra("product", product)
                    putExtra("quantity", viewModel.qty.value)
                }
            )
        }

        binding.btnShare.setOnClickListener {
            shareProduct()
        }
    }

    private fun observeViewModel() {
        viewModel.qty.observe(this) { quantity ->
            binding.tvQty.text = quantity.toString()
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Display
    // ─────────────────────────────────────────────────────────────

    private fun displayProductData(product: Product) {
        with(binding) {
            tvProductName.text  = product.productName
            tvDescription.text  = product.description
            tvSellingPrice.text = "₹${product.productPrice}"
            tvVolume.text       = product.sizes.getOrNull(0)?.label ?: ""
            tvVolume.isSelected = true

            tvMrp.apply {
                text       = "₹${product.dairyMrp}"
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            bindDiscountBadge(
                price = product.productPrice.toDoubleOrNull() ?: 0.0,
                mrp   = product.dairyMrp.toDoubleOrNull()    ?: 0.0
            )

            Glide.with(this@ProductDetailsActivity)
                .load(product.imageUrl)
                .into(ivProductImage)
        }
    }

    private fun bindDiscountBadge(price: Double, mrp: Double) {
        if (mrp > price) {
            val discount = ((mrp - price) / mrp * 100).toInt()
            binding.tvDiscount.text       = "$discount% OFF"
            binding.tvDiscount.visibility = View.VISIBLE
        } else {
            binding.tvDiscount.visibility = View.GONE
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Share
    // ─────────────────────────────────────────────────────────────

    private fun shareProduct() {
        val shareText = buildShareText()

        Glide.with(this)
            .asBitmap()
            .load(product.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareImageAndText(resource, shareText)
                }
                override fun onLoadCleared(placeholder: Drawable?) = Unit
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    shareTextOnly(shareText)
                }
            })
    }

    private fun buildShareText(): String = """
        🛒 Check out this product on Freshyzo!
        
        Product : ${product.productName}
        Price   : ₹${product.productPrice}
        
        ${product.shortDesc}
        
        Download Freshyzo App: https://play.google.com/store/search?q=freshyzo&c=apps&hl=en_IN
    """.trimIndent()

    private fun shareTextOnly(text: String) {
        startActivity(
            AndroidIntent.createChooser(
                AndroidIntent(AndroidIntent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(AndroidIntent.EXTRA_TEXT, text)
                },
                "Share via"
            )
        )
    }

    private fun shareImageAndText(bitmap: Bitmap, text: String) {
        try {
            val imageFile = saveBitmapToCache(bitmap)
            val contentUri = FileProvider.getUriForFile(
                this, "${packageName}.fileprovider", imageFile
            )

            startActivity(
                AndroidIntent.createChooser(
                    AndroidIntent(AndroidIntent.ACTION_SEND).apply {
                        addFlags(AndroidIntent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(contentUri, contentResolver.getType(contentUri))
                        putExtra(AndroidIntent.EXTRA_STREAM, contentUri)
                        putExtra(AndroidIntent.EXTRA_TEXT, text)
                        type = "image/png"
                    },
                    "Share via"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            shareTextOnly(text)   // graceful fallback
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val cacheDir = File(cacheDir, "images").also { it.mkdirs() }
        val file = File(cacheDir, "product_image.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file
    }

    // ─────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.95f).scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).duration = 100
            }
    }
}