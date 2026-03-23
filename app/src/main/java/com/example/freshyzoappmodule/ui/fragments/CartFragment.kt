package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.DialogSuccessLottieBinding
import com.example.freshyzoappmodule.databinding.FragmentCartBinding
import com.example.freshyzoappmodule.helper.CustomDatePickerDialog
import com.example.freshyzoappmodule.ui.activity.HomeActivity
import com.example.freshyzoappmodule.ui.adapter.CartAdapter
import com.example.freshyzoappmodule.ui.viewmodel.ProductSubscribeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartFragment : Fragment() {
    private val viewModel: ProductSubscribeViewModel by viewModels()
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartRepository: CartRepository
    private lateinit var customDatePickerDialog: CustomDatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartRepository = CartRepository(requireContext())
        customDatePickerDialog = CustomDatePickerDialog()

        setupCartList()
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.tvSelectedDate.text = state.startDate
            binding.tvDeliveryBegins.text = state.deliveryBeginsText
        }
    }

    private fun setupClickListeners() {
        val dateClickListener = View.OnClickListener {
            showDatePicker()
        }
        binding.layoutDateSelector.setOnClickListener(dateClickListener)
        binding.btnEditDate.setOnClickListener(dateClickListener)

        binding.btnShopNow.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.nav_product
        }

        binding.btnSubscribeNow.setOnClickListener {
            showSuccessDialog("Order Placed!", "Your order has been successfully placed.\nThank you for shopping with us!")
        }
    }

    private fun showSuccessDialog(title: String, message: String) {
        val dialogBinding = DialogSuccessLottieBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.tvSuccessTitle.text = title
        dialogBinding.tvSuccessMessage.text = message

        dialogBinding.btnDone.setOnClickListener {
            dialog.dismiss()
            
            // Clear cart globally through HomeActivity to sync across all screens
            (activity as? HomeActivity)?.clearSharedCart()
            
            // Refresh local UI
            showEmptyUI()
            
            // Navigate to home
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.nav_product
        }

        dialog.show()
    }

    private fun showDatePicker() {
        // Passing requireActivity() as AppCompatActivity fixes the type mismatch error
        customDatePickerDialog.showMaterialDatePicker(requireActivity() as AppCompatActivity) { formattedDate, dayName ->
            viewModel.updateDateSelection(formattedDate, dayName)
        }
    }

    private fun setupCartList() {
        val cartState = cartRepository.getCartState()

        if (cartState != null && cartState.productDetails.isNotEmpty()) {
            binding.rvAddedProductInCart.visibility = View.VISIBLE
            binding.cardPriceDetails.visibility = View.VISIBLE
            binding.llElements.visibility = View.VISIBLE
            binding.btnSubscribeNow.visibility = View.VISIBLE

            binding.tvCartIsEmpty.visibility = View.GONE
            binding.animEmptyCart.visibility = View.GONE
            binding.tvFreshness.visibility = View.GONE
            binding.btnShopNow.visibility = View.GONE

            cartAdapter = CartAdapter(
                productDetails = cartState.productDetails,
                quantities = cartState.productQuantities,
                cartRepository = cartRepository,
                onCartUpdated = {
                    updateSummary()
                }
            )
            
            binding.rvAddedProductInCart.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAddedProductInCart.adapter = cartAdapter

            updateSummary()
        } else {
            showEmptyUI()
        }
    }

    private fun showEmptyUI() {
        binding.llElements.visibility = View.GONE
        binding.cardPriceDetails.visibility = View.GONE
        binding.btnSubscribeNow.visibility = View.GONE
        binding.btnShopNow.visibility = View.VISIBLE
        binding.tvCartIsEmpty.visibility = View.VISIBLE
        binding.animEmptyCart.visibility = View.VISIBLE
        binding.tvFreshness.visibility = View.VISIBLE
    }

    private fun updateSummary() {
        val cartState = cartRepository.getCartState()
        if (cartState != null) {
            val totalMRP = cartState.totalPrice + cartState.discount

            binding.tvTotalMRP.text = "₹${"%.2f".format(totalMRP)}"
            binding.tvTotalPayable.text = "₹${"%.2f".format(cartState.totalPrice)}"
            binding.tvDiscount.text = "- ₹${"%.2f".format(cartState.discount)}"
            binding.tvSavings.text = "🎊 You're saving ₹${"%.2f".format(cartState.discount)} on this order!"
            binding.tvItemCount.text = "${cartState.itemsCount} items"

            if (cartState.productDetails.isEmpty()) {
                showEmptyUI()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
