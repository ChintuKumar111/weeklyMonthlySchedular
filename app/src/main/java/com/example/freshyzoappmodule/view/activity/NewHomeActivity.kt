package com.example.freshyzoappmodule.view.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.example.freshyzoappmodule.view.adapter.ImageSliderAdapter
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.tabs.TabLayoutMediator

class NewHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()

        window.decorView.post {
            showHomeTour()
        }
    }

    private fun showHomeTour() {
        TapTargetSequence(this)
            .targets(
                // Your TapTarget views here
            )
            .continueOnCancel(true)
            .start()
    }

    private fun setupUi() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sample images for the slider
        val images = listOf(
            "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w",
            "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png",
            "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg"
        )

        val adapter = ImageSliderAdapter(images)
        binding.productSliderCart.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.productSliderCart) { tab, position ->
            // No text to be displayed
        }.attach()
    }
}



//    private fun showHomeTour() {
//
//        TapTargetSequence(this)
//            .targets(
//
//                TapTarget.forView(binding.img1,
//                    "Daily Offers",
//                    "Check discounts on dairy products here")
//                    .cancelable(true),
//
//                TapTarget.forView(binding.img2,
//                    "Categories",
//                    "Browse milk, paneer, curd and more")
//                    .cancelable(true),
//
//                TapTarget.forView(binding.img3,
//                    "Wallet",
//                    "Add money for fast checkout")
//                    .cancelable(true),
//
//                TapTarget.forView(binding.img4,
//                    "Notifications",
//                    "See delivery updates and offers")
//                    .cancelable(true),
//
//                TapTarget.forView(binding.img5,
//                    "Dairy Blogs",
//                    "Read how products are made")
//                    .cancelable(true),
//
//                TapTarget.forView(binding.img6,
//                    "Milk Quality",
//                    "View daily milk test reports")
//                    .cancelable(true)
//            )
//            .continueOnCancel(true) // ← this acts like Skip
//            .start()
//    }






