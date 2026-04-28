package com.shyamdairyfarm.user.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.ActivityCustomerSignUpBinding
import com.shyamdairyfarm.user.ui.fragments.signUp.SignUpUserFragment

class CustomerSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}