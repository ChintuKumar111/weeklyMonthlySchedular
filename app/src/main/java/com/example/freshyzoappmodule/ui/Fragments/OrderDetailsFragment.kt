package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import com.example.freshyzoappmodule.data.model.DeliveryStatus
import com.example.freshyzoappmodule.databinding.FragmentOrderDetailsBinding
import kotlin.getValue
class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: OrderDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(args.delivery)
        //setupListeners()
    }

    private fun setupUI(item: OrderHistoryModel) {
        val ctx = requireContext()

//        // ── Header ──────────────────────────────────────────
//        binding.toolbar.title = "Order #${item.txnId}"
//        binding.tvOrderId.text = "Order ID: #${item.txnId}"
//        binding.tvOrderDate.text = "Date: ${item.date}"

        // ── Status ──────────────────────────────────────────
        applyStatus(item.status)

//        // ── Product Info ────────────────────────────────────
//        binding.tvProductEmoji.text = item.emoji
//        binding.tvProductName.text = item.productName
//        binding.tvBrandName.text = item.brandName
//        binding.tvSizeQty.text = "${item.size} × ${item.quantity} unit${if (item.quantity > 1) "s" else ""}"

//        // Image Box Background
//        binding.productImageBox.setBackgroundResource(
//            when (item.productType) {
//                ProductType.MILK -> R.drawable.bg_product_image_milk
//                ProductType.GHEE -> R.drawable.bg_product_image_ghee
//            }
//        )

        // ── Payment ─────────────────────────────────────────
//        binding.tvAmountPaid.text = "₹${String.format("%.2f", item.amountPaid)}"
    }

    private fun applyStatus(status: DeliveryStatus) {
        val ctx = requireContext()
        when (status) {
            DeliveryStatus.PLACED -> {
                binding.tvStatus.text = "● Placed"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.emerald))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_placed)
            }
            DeliveryStatus.PENDING -> {
                binding.tvStatus.text = "⏳ Pending"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.amber))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_pending)
            }
            DeliveryStatus.CANCELLED -> {
                binding.tvStatus.text = "✕ Cancelled"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.coral))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_cancelled)
            }
        }
    }

//    private fun setupListeners() {
//        binding.toolbar.setNavigationOnClickListener {
//            findNavController().popBackStack()
//        }
//
//        binding.btnSupport.setOnClickListener {
//            // Navigate to support or help center
//            findNavController().navigate(R.id.action_nav_account_to_complaintAssistanceFragment)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
