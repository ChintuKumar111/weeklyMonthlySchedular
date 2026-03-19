package com.example.freshyzoappmodule.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.databinding.BottomSheetEditAddressBinding
import com.example.freshyzoappmodule.databinding.FragmentAddressBinding
import com.example.freshyzoappmodule.ui.activity.SelectLocationActivity
import com.example.freshyzoappmodule.ui.viewmodel.AddressViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val viewModel: AddressViewModel by viewModel()
    private val selectLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val address = data?.getStringExtra("address") ?: ""
            val lat = data?.getDoubleExtra("lat", 0.0) ?: 0.0
            val lng = data?.getDoubleExtra("lng", 0.0) ?: 0.0
            
            if (address.isNotEmpty()) {
                viewModel.updateAddress(address, lat, lng)
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
        
        setupObservers()
        setupClickListeners()
        
        viewModel.loadSavedAddress()
    }

    private fun setupObservers() {
        // Update UI when address data is received
        viewModel.userAddress.observe(viewLifecycleOwner) { address ->
            binding.tvAddress.text = address.fullAddress
            binding.tvUserName.text = if (address.name.isNotEmpty()) address.name else "Levi Ackerman"
            binding.tvPhone.text = if (address.phone.isNotEmpty()) address.phone else "+91 91795 93730"
        }

        // Handle Loading State: Hide content and show ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                binding.llBottomButton.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                binding.llBottomButton.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnEdit.setOnClickListener {
            showEditAddressBottomSheet()
        }

        binding.cardAddNewAddress.setOnClickListener {
            openMapLocationPicker()
        }

        binding.btnCurrentLocation.setOnClickListener {
            openMapLocationPicker()
        }
    }

    private fun showEditAddressBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        bottomSheetDialog = dialog
        val sheetBinding = BottomSheetEditAddressBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.etAddress.setText(binding.tvAddress.text)
        sheetBinding.etAddress.setSelection(sheetBinding.etAddress.text?.length ?: 0)

        sheetBinding.btnSaveAddress.setOnClickListener {
            val newAddress = sheetBinding.etAddress.text.toString().trim()
            if (newAddress.isNotEmpty()) {
                viewModel.updateAddress(newAddress)
                dialog.dismiss()
            } else {
                sheetBinding.tilAddress.error = "Address cannot be empty"
            }
        }

        dialog.show()
    }

    private fun openMapLocationPicker() {
        val intent = Intent(requireContext(), SelectLocationActivity::class.java)
        selectLocationLauncher.launch(intent)
    }

    override fun onDestroyView() {
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
        super.onDestroyView()
        _binding = null
    }
}
