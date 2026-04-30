package com.shyamdairyfarm.user.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.ActivityCustomerSignUpBinding
import com.shyamdairyfarm.user.ui.fragments.signUp.SignUpUserFragment
import androidx.navigation.fragment.NavHostFragment
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomerSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerSignUpBinding
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkProgress()
    }

    private fun checkProgress() {
        if (!viewModel.name.value.isNullOrEmpty()) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHost) as NavHostFragment
            val navController = navHostFragment.navController
            
            // Navigate to Map screen if name already exists
            navController.navigate(R.id.action_signUpUserFragment_to_signUpAddressMapsFragment)
        }
    }
}
