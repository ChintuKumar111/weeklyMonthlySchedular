package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.FragmentCartBinding
import com.example.freshyzoappmodule.ui.adapter.CartAdapter

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartRepository: CartRepository

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

        setupCartList()
    }

    private fun setupCartList() {
        val cartState = cartRepository.getCartState()

        if (cartState != null && cartState.products.isNotEmpty()) {
            binding.rvAddedProductInCart.visibility = View.VISIBLE
            binding.cardPriceDetails.visibility = View.VISIBLE
            // binding.emptyLayout.visibility = View.GONE

            cartAdapter = CartAdapter(
                products = cartState.products,
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
            binding.rvAddedProductInCart.visibility = View.GONE
            binding.cardPriceDetails.visibility = View.GONE
            // binding.emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun updateSummary() {
        val cartState = cartRepository.getCartState()
        if (cartState != null) {
            binding.tvTotalMRP.text = "₹${cartState.totalPrice}"
            // Update other summary fields if they exist in FragmentCartBinding
            // binding.tvTotalAmount.text = "₹${cartState.totalPrice}"
            
            if (cartState.products.isEmpty()) {
                binding.rvAddedProductInCart.visibility = View.GONE
                binding.cardPriceDetails.visibility = View.GONE
                // binding.emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
