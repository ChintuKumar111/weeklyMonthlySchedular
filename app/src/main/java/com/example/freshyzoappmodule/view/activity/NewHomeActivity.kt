package com.example.freshyzoappmodule.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.example.freshyzoappmodule.view.Fragments.NewHome_Fragment
import com.example.freshyzoappmodule.view.adapter.ImageSliderAdapter
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.tabs.TabLayoutMediator

class NewHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(NewHome_Fragment())
        }

        // Disable icon tinting to show original colors
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(NewHome_Fragment())
                    true
                }
                // TODO: Add other fragments for navigation
                /*
                R.id.navigation_product -> {
                    replaceFragment(ProductFragment())
                    true
                }
                R.id.navigation_wallet -> {
                    replaceFragment(WalletFragment())
                    true
                }
                R.id.navigation_account -> {
                    replaceFragment(AccountFragment())
                    true
                }
                R.id.navigation_cart -> {
                    replaceFragment(CartFragment())
                    true
                }
                */
                else -> false
            }
        }

        setupUi()
        showHomeTour()
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
        // Sample images for the slider
        val images = listOf(
            "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w",
            "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png",
            "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg"
        )

        val adapter = ImageSliderAdapter(images)
//        binding.productSliderCart.adapter = adapter
//
//        TabLayoutMediator(binding.tabLayout, binding.productSliderCart) { tab, position ->
//            // No text to be displayed
//        }.attach()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
