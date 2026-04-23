package com.shyamdairyfarm.user.ui.activity

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.ProductDetails
import com.shyamdairyfarm.user.data.model.ProductMedia
import com.shyamdairyfarm.user.data.objects.FaqManager
import com.shyamdairyfarm.user.databinding.ActivityProductDetailsBinding
import com.shyamdairyfarm.user.extensions.imageUrl
import com.shyamdairyfarm.user.extensions.variant
import com.shyamdairyfarm.user.ui.activity.comparison.ComparisonBinder
import com.shyamdairyfarm.user.ui.activity.comparison.ComparisonData
import com.shyamdairyfarm.user.ui.adapter.FaqAdapter
import com.shyamdairyfarm.user.ui.adapter.ProductMediaAdapter
import com.shyamdairyfarm.user.ui.viewmodel.ProductDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import android.content.Intent as AndroidIntent

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productDetails: ProductDetails
    private var mediaAdapter: ProductMediaAdapter? = null

    private val viewModel: ProductDetailsViewModel by viewModel()
    private val faqAdapter = FaqAdapter()

    // ─────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentProduct = parseProductFromIntent() ?: run { finish(); return }
        productDetails = intentProduct
        viewModel.setProduct(intentProduct)

        setupFaq(intentProduct.productName)
        setupComparison()
        setupClickListeners()
        observeViewModel()
        displayProductData(intentProduct)
        setupReviews()
    }

    override fun onPause() {
        super.onPause()
        mediaAdapter?.pauseAllVideos()
    }

    // ─────────────────────────────────────────────────────────────
    //  Setup
    // ─────────────────────────────────────────────────────────────

    private fun parseProductFromIntent(): ProductDetails? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", ProductDetails::class.java)
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
                    putExtra("product", productDetails)
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
            binding.btnMinus.apply {
                isEnabled = quantity > 2
                alpha = if (quantity > 2) 1.0f else 0.5f
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Display
    // ─────────────────────────────────────────────────────────────

    private fun displayProductData(productDetails: ProductDetails) {
        with(binding) {
            tvProductName.text  = productDetails.productName
            tvDescription.text  = productDetails.description
            tvSellingPrice.text = "₹${productDetails.productPrice}"
            tvVolume.text       = productDetails.variant.getOrNull(0)?.label ?: ""
            tvVolume.isSelected = true

            tvMrp.apply {
                text       = "₹${productDetails.dairyMrp}"
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            bindDiscountBadge(
                price = productDetails.productPrice.toDoubleOrNull() ?: 0.0,
                mrp   = productDetails.dairyMrp.toDoubleOrNull()    ?: 0.0
            )

            // Setup Media Slider with data
            val images = listOf(productDetails.imageUrl) // Base image
            // Mocking a video for testing purposes as per requirement
            val videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            setupMediaSlider(images, videoUrl)
        }
    }

    private fun setupMediaSlider(images: List<String>, videoUrl: String?) {
        val mediaList = mutableListOf<ProductMedia>()

        images.forEach { url ->
            mediaList.add(ProductMedia(url, isVideo = false))
        }

        if (!videoUrl.isNullOrEmpty()) {
            mediaList.add(ProductMedia(videoUrl, isVideo = true))
        }

        val adapter = ProductMediaAdapter(mediaList)
        mediaAdapter = adapter
        binding.vpProductMedia.adapter = adapter

        binding.vpProductMedia.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val isVideoPage = mediaList[position].isVideo
                if (isVideoPage) {
                    adapter.playVideoAt(position)
                } else {
                    adapter.pauseAllVideos()
                }
            }
        })

        if (mediaList.size > 1) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.vpProductMedia)
        } else {
            binding.dotsIndicator.visibility = View.GONE
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

    private fun setupReviews() {
        val reviews = listOf(
            ReviewData("Sachin", "S", "Highly recommended! The quality is top-notch and the delivery is always on time. Best A2 milk in town.", "Feb 18, 2026", "★ 5.0", "18"),
            ReviewData("Jay", "J", "Very fresh and creamy. I've switched from regular packets to Freshyzo and the difference is amazing.", "Feb 16, 2026", "★ 4.9", "12"),
            ReviewData("Prakash", "P", "Great service! The app is easy to use and the subscription model is very convenient for daily needs.", "Feb 14, 2026", "★ 4.8", "9")
        )

        val reviewContainers = listOf(
            binding.root.findViewById<View>(R.id.llReviewContainer).run {
                val linearLayout = this as android.widget.LinearLayout
                listOf(linearLayout.getChildAt(0), linearLayout.getChildAt(1), linearLayout.getChildAt(2))
            }
        ).flatten()

        reviews.forEachIndexed { index, review ->
            if (index < reviewContainers.size) {
                val view = reviewContainers[index]
                view.findViewById<TextView>(R.id.tvReviewerName).text = review.name
                view.findViewById<TextView>(R.id.tvAvatar).text = review.avatar
                view.findViewById<TextView>(R.id.tvReviewText).text = review.text
                view.findViewById<TextView>(R.id.tvReviewDate).text = review.date
                view.findViewById<TextView>(R.id.tvStarRating).text = review.rating
                view.findViewById<TextView>(R.id.btnHelpfulYes).text = "👍 ${review.helpfulCount}"
            }
        }
    }

    data class ReviewData(
        val name: String,
        val avatar: String,
        val text: String,
        val date: String,
        val rating: String,
        val helpfulCount: String
    )

    // ─────────────────────────────────────────────────────────────
    //  Share
    // ─────────────────────────────────────────────────────────────

    private fun shareProduct() {
        val shareText = buildShareText()

        Glide.with(this)
            .asBitmap()
            .load(productDetails.imageUrl)
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
        
        Product : ${productDetails.productName}
        Price   : ₹${productDetails.productPrice}
        
        ${productDetails.shortDesc}
        
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
            shareTextOnly(text)
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val cacheDir = File(cacheDir, "images").also { it.mkdirs() }
        val file = File(cacheDir, "product_image.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file
    }

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.95f).scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).duration = 100
            }
    }
}
