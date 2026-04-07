package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentWalletBinding
import com.example.freshyzoappmodule.ui.activity.HomeActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class WalletFragment : Fragment(), PaymentResultListener {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Checkout.preload(requireContext())

        setupToolbar()
        setupRechargeButton()
        setupQuickAmountChips()
        startPulseDot()

        // 🔥 Disable button initially
        binding.btnRecharge.isEnabled = false
        binding.btnRecharge.alpha = 0.5f

        binding.etAmount.addTextChangedListener {
            val amount = it.toString().toIntOrNull()

            if (amount != null && amount > 0) {
                binding.btnRecharge.isEnabled = true
                binding.btnRecharge.alpha = 1f
            } else {
                binding.btnRecharge.isEnabled = false
                binding.btnRecharge.alpha = 0.5f
            }
        }
    }

    private fun setupToolbar() {
        val currentDestinationId = findNavController().currentDestination?.id
        
        // Show back button only if we are in the "sub" destination (opened from Account)
        if (currentDestinationId == R.id.walletFragment_sub) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_back)
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            // Adjust title margin if back icon is present
            val titleView = binding.toolbar.getChildAt(0) as? android.widget.TextView
            titleView?.let {
                val params = it.layoutParams as? ViewGroup.MarginLayoutParams
                params?.marginStart = 0
                it.layoutParams = params
            }
        } else {
            binding.toolbar.navigationIcon = null
        }
    }

    // ✅ Razorpay Payment Start
    private fun startPayment(amount: Int) {
        // Hide bars before opening Razorpay
        (activity as? HomeActivity)?.setFullScreenMode(true)

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_RiOXIkbKD9FNa9")

        try {
            val options = JSONObject().apply {
                put("name", "Freshyzo")
                put("description", "Wallet Recharge")
                put("currency", "INR")
                put("amount", amount * 100) // MUST be Int (paise)
                put("theme.color", "#17ab6f")

                val prefill = JSONObject()
                prefill.put("email", "test@freshyzo.com")
                prefill.put("contact", "9876543210")
                put("prefill", prefill)
            }

            checkout.open(requireActivity(), options)

        } catch (e: Exception) {
            // Restore bars if there's an error opening
            (activity as? HomeActivity)?.setFullScreenMode(false)
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }

    // ✅ Payment Success Callback
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        // Restore bars
        (activity as? HomeActivity)?.setFullScreenMode(false)
        
        if (!isAdded || _binding == null) return

        Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()
        binding.etAmount.text?.clear()

        Log.d("Razorpay", "Payment ID: $razorpayPaymentID")

        // 👉 Here call ViewModel to update wallet
    }

    // ✅ Payment Error Callback
    override fun onPaymentError(code: Int, response: String?) {
        // Restore bars
        (activity as? HomeActivity)?.setFullScreenMode(false)

        if (!isAdded || _binding == null) return

        if (code == Checkout.PAYMENT_CANCELED) {
            Toast.makeText(requireContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_SHORT).show()
            Log.e("Razorpay", "Error $code: $response")
        }
    }

    // ✅ Recharge Button
    private fun setupRechargeButton() {
        binding.btnRecharge.setOnClickListener {

            val amount = binding.etAmount.text.toString().toIntOrNull()

            if (amount == null || amount <= 0) {
                binding.layoutAmountInput.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.offer_icon)
                Toast.makeText(requireContext(), "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.layoutAmountInput.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_amount_input)

            startPayment(amount)
        }
    }

    // ✅ Quick Amount Chips
    private fun setupQuickAmountChips() {
        val amounts = listOf(1000, 2000, 3000, 5000)
        val chips = listOf(
            binding.chip1000,
            binding.chip2000,
            binding.chip3000,
            binding.chip5000
        )

        chips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                chips.forEach { it.isSelected = false }
                chip.isSelected = true
                binding.etAmount.setText(amounts[index].toString())
                

            }
        }
    }
    // ✅ Pulse Animation
    private fun startPulseDot() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)



        binding.pulseDot.startAnimation(anim)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ensure bars are restored if fragment is destroyed while checkout is open
        (activity as? HomeActivity)?.setFullScreenMode(false)
        _binding = null
    }
}
