package com.example.freshyzoappmodule.view.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.databinding.ActivityProductLoadActivityBinding
import com.example.freshyzoappmodule.view.Adapter.ProductAdapter
import com.example.freshyzoappmodule.viewmodel.HomeViewModel
import com.example.freshyzoappmodule.data.model.CartStateModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductLoadActivityBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private var cartItemsCount = 0
    private var cartTotalPrice = 0.0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductLoadActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()
        setupUI()
        observeData()
        viewModel.loadProducts()

        binding.iconNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.btnSearchBar.setOnClickListener {
            val list = viewModel.productList.value
            if (list.isNullOrEmpty()) {
                Toast.makeText(this, "Products not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putParcelableArrayListExtra("product_list", ArrayList(list))
            startActivity(intent)
        }

        binding.cartPreview.setOnViewCartClickListener {
            // Handle View Cart click
            Toast.makeText(this, "Opening Cart...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupUI() {
        adapter = ProductAdapter(emptyList()) { product, delta ->
            handleCartUpdate(product, delta)
        }
        binding.rvProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduct.adapter = adapter
    }

    private fun observeData() {
        binding.progressCircular.visibility = View.VISIBLE
        viewModel.productList.observe(this) { list ->
            adapter.updateList(list)
            if (list.isNotEmpty()) {
                binding.progressCircular.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCartUpdate(product: ProductModel, delta: Int) {
        cartItemsCount += delta
        val price = product.product_price.toDoubleOrNull() ?: 0.0
        cartTotalPrice += (price * delta)

        if (cartItemsCount < 0) cartItemsCount = 0
        if (cartTotalPrice < 0) cartTotalPrice = 0.0

        binding.cartPreview.showCart(CartStateModel(cartItemsCount, cartTotalPrice))
    }
}
