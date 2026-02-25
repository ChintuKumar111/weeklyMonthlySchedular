//package com.example.freshyzoappmodule.helper
//import android.animation.ObjectAnimator
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.example.freshyzoappmodule.R
//import com.example.freshyzoappmodule.databinding.FragmentWalletBinding
//
//class WalletFragment : Fragment() {
//
//    private var _binding: FragmentWalletBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentWalletBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupQuickAmountChips()
//        startPulseDot()
//        setupRechargeButton()
//    }
//
//    // ── Quick amount chips ──────────────────────────────────
//    private val chipIds by lazy {
//        listOf(binding.chip1000, binding.chip2000, binding.chip3000, binding.chip5000)
//    }
//
//    private fun setupQuickAmountChips() {
//        val amounts = listOf(1000, 2000, 3000, 5000)
//        chipIds.forEachIndexed { i, tv ->
//            tv.setOnClickListener {
//                chipIds.forEach { c -> c.isSelected = false }
//                tv.isSelected = true
//                binding.etAmount.setText(amounts[i].toString())
//                binding.etAmount.setSelection(binding.etAmount.text.length)
//            }
//        }
//    }
//
//    // ── Pulsing low-balance dot ─────────────────────────────
//    private fun startPulseDot() {
//        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
//        binding.pulseDot.startAnimation(anim)
//    }
//
//    // ── Recharge button ─────────────────────────────────────
//    private fun setupRechargeButton() {
//        binding.btnRecharge.setOnClickListener {
//            val amountStr = binding.etAmount.text.toString()
//            if (amountStr.isEmpty() || amountStr.toDoubleOrNull() == null) {
//                binding.layoutAmountInput.background =
//                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_amount_input_error)
//                return@setOnClickListener
//            }
//            // Reset border
//            binding.layoutAmountInput.background =
//                ContextCompat.getDrawable(requireContext(), R.drawable.bg_amount_input)
//
//            // TODO: call your ViewModel / API here
//            val amount = amountStr.toDouble()
//            initiateRecharge(amount)
//        }
//    }
//
//    private fun initiateRecharge(amount: Double) {
//        // Navigate to payment screen or call Razorpay / payment gateway
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}