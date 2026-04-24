package com.shyamdairyfarm.user.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.shyamdairyfarm.user.databinding.ActivityAuthBinding
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
         val authViewModel: AuthViewModel by viewModel()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.isLoggedIn.observe(this) { isLoggedIn ->

            if (isLoggedIn) {
                // ✅ User is logged in → go to next screen
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
}