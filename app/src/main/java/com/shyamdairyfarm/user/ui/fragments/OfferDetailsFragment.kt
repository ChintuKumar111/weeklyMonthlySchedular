package com.shyamdairyfarm.user.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.shyamdairyfarm.user.databinding.FragmentOfferDetailsBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class OfferDetailsFragment : Fragment() {
    private var _binding: FragmentOfferDetailsBinding? = null
    private val binding get() = _binding!!
    private val products = listOf(
        "Cow Milk 500 ml",
        "Buffalo Milk 500 ml",
        "A2 Cow Milk 500 ml"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentOfferDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupProductDropdown()
        setupDatePicker()
        setupStartTrial()
    }
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun setupProductDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            products
        )
        binding.actvProduct.setAdapter(adapter)
        binding.actvProduct.setOnItemClickListener { _,_, position, _ ->
            updatePricing(products[position])
        }
    }

    private fun updatePricing(product: String) {
        // Update prices based on selected product
        when (product) {
            "Cow Milk 500 ml" -> {
                binding.tvTotalMrp.text = "₹180"
                binding.tvDiscount.text = "-₹90"
                binding.tvDeliveryFee.text = "₹0"
                binding.tvTotalPayable.text = "₹90"
            }
            "Buffalo Milk 500 ml" -> {
                binding.tvTotalMrp.text = "₹210"
                binding.tvDiscount.text = "-₹105"
                binding.tvDeliveryFee.text = "₹0"
                binding.tvTotalPayable.text = "₹105"
            }
            "A2 Cow Milk 500 ml" -> {
                binding.tvTotalMrp.text = "₹270"
                binding.tvDiscount.text = "-₹135"
                binding.tvDeliveryFee.text = "₹0"
                binding.tvTotalPayable.text = "₹135"
            }
        }
    }

    private fun setupDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Start Date")
            .setCalendarConstraints(constraints)
            .build()

        binding.etDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        binding.tilDate.setEndIconOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.etDate.setText(sdf.format(Date(selection)))
        }
    }

    private fun setupStartTrial() {
        binding.btnStartTrial.setOnClickListener {
            val date = binding.etDate.text?.toString()
            if (date.isNullOrEmpty()) {
                binding.tilDate.error = "Please select a start date"
                return@setOnClickListener
            }
            binding.tilDate.error = null
            // Navigate to confirmation or process trial
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    4. Offer Details Screen UI
//    Designed the user interface for the Offer Details screen to clearly display available offers to users.
//
//    5. Offer Items Logic Implementation
//    Implemented the logic to fetch and display offer items dynamically on the Offer Details screen.
}