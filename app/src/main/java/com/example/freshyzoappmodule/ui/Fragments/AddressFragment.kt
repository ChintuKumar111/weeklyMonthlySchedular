package com.example.freshyzoappmodule.ui.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.databinding.BottomSheetEditAddressBinding
import com.example.freshyzoappmodule.databinding.FragmentAddressBinding
import com.example.freshyzoappmodule.ui.activity.SelectLocationActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    // Launcher to handle result from SelectLocationActivity (for current location/map picking)
    private val selectLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val address = data?.getStringExtra("address")
            if (!address.isNullOrEmpty()) {
                // Reflect the map-selected address in the TextView
                binding.tvAddress.text = address
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Edit button on the address card - Now opens a BottomSheet for manual editing
        binding.btnEdit.setOnClickListener {
            showEditAddressBottomSheet()
        }

        // Add new address card - Still opens map for convenience
        binding.cardAddNewAddress.setOnClickListener {
            openMapLocationPicker()
        }

        // Bottom button: Use Current Location - Opens map
        binding.btnCurrentLocation.setOnClickListener {
            openMapLocationPicker()
        }
    }

    private fun showEditAddressBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetEditAddressBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // Pre-fill the current address from the UI
        sheetBinding.etAddress.setText(binding.tvAddress.text)
        sheetBinding.etAddress.setSelection(sheetBinding.etAddress.text?.length ?: 0)

        sheetBinding.btnSaveAddress.setOnClickListener {
            val newAddress = sheetBinding.etAddress.text.toString().trim()
            if (newAddress.isNotEmpty()) {
                // Reflect the manually edited address in the Fragment's TextView
                binding.tvAddress.text = newAddress
                bottomSheetDialog.dismiss()
            } else {
                sheetBinding.tilAddress.error = "Address cannot be empty"
            }
        }

        bottomSheetDialog.show()
    }

    private fun openMapLocationPicker() {
        val intent = Intent(requireContext(), SelectLocationActivity::class.java)
        selectLocationLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
