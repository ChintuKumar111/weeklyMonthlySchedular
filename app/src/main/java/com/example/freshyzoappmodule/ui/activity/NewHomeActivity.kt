package com.example.freshyzoappmodule.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivityNewHomeBinding
import com.example.freshyzoappmodule.ui.Fragments.NewHome_Fragment
import com.example.freshyzoappmodule.ui.Fragments.ProductSectionFragment
import com.getkeepsafe.taptargetview.TapTargetSequence

class NewHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(NewHome_Fragment())
        }

//        FirebaseFirestore.getInstance()
//            .collection("test")
//            .add(hashMapOf("name" to "Levi Ackerman"))


        // Disable icon tinting to show original colors
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(NewHome_Fragment())
                    true
                }
                // TODO: Add other fragments for navigation

                R.id.nav_product -> {
                    replaceFragment(ProductSectionFragment())
                    true
                }


                R.id.nav_wallet -> {
                    startActivity(Intent(this, ChatListActivity::class.java))
                    true
                }
                /*
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





    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
