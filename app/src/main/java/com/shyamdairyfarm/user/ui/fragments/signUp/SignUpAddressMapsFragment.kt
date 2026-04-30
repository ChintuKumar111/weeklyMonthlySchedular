package com.shyamdairyfarm.user.ui.fragments.signUp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.FragmentSignUpAddressMapsBinding
import com.shyamdairyfarm.user.helper.LocationManager
import com.shyamdairyfarm.user.ui.activity.HomeActivity
import com.shyamdairyfarm.user.ui.activity.utils.DialogUtils
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpAddressMapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSignUpAddressMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private var selectedLatLng: LatLng? = null

    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpAddressMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupMap()

        locationManager = LocationManager(
            activity = requireActivity(),
            onLocationStatusChanged = { isLoading ->
                binding.llLoadingLocation.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            },
            onLocationReceived = { latLng ->
                if (::mMap.isInitialized) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    )
                }
            }
        )

        observeViewModel()
        setupButtonClick()

        binding.fabMyLocation.setOnClickListener {
            checkPermissionAndRequestLocation()
        }
    }

    private fun observeViewModel() {

        viewModel.address.observe(viewLifecycleOwner) {
            binding.tvSelectedAddress.text = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newCustomerSignUpState.collectLatest { state ->
                when (state) {
                    is UiState.Error -> {
                        setLoading(false)

                        DialogUtils.showErrorDialog(requireContext(), msg = state.message) {
                            viewModel.logout()
                        }
                    }

                    UiState.Idle -> setLoading(false)

                    UiState.Loading -> setLoading(true)

                    is UiState.Success<*> -> {
                        setLoading(false)

                        val intent = Intent(context, HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    is UiState.ExpiredToken -> {}
                    is UiState.UnauthorizedAccess -> {}
                }
            }
        }
    }

    private fun setupButtonClick() {

        binding.btnRegister.setOnClickListener {
            viewModel.registerNewCustomer()
        }

        binding.btnEditAddress.setOnClickListener {
            showEditAddressBottomSheet()
        }
    }

    private fun showEditAddressBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_edit_address, null)
        dialog.setContentView(view)

        val etAddress = view.findViewById<TextInputEditText>(R.id.etAddress)
        val btnSaveAddress = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSaveAddress)

        etAddress.setText(viewModel.address.value)

        btnSaveAddress.setOnClickListener {
            val newAddress = etAddress.text.toString().trim()
            if (newAddress.isNotEmpty()) {
                viewModel.updateAddress(newAddress)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading

        if (isLoading) {
            binding.btnRegister.text = ""
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.alpha = 0.7f
        } else {
            binding.btnRegister.text = "Confirm Location"
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.alpha = 1f
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = false
            isCompassEnabled = true
            isRotateGesturesEnabled = true
        }

        checkPermissionAndRequestLocation()

        mMap.setOnCameraMoveStartedListener {
            binding.tvSelectedAddress.text = "Fetching address..."
        }

        mMap.setOnCameraIdleListener {
            selectedLatLng = mMap.cameraPosition.target

            selectedLatLng?.let {
                viewModel.fetchAddress(it)
                viewModel.updateLatLang(it.latitude, it.longitude)

                binding.tvLatLng.apply {
                    text = "${it.latitude}, ${it.longitude}"
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
                as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.map, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun checkPermissionAndRequestLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
            locationManager.reset()
            locationManager.checkLocationSettings(2001)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        try {
            mMap.isMyLocationEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                enableMyLocation()
                locationManager.checkLocationSettings(2001)
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager.removeUpdates()
        _binding = null
    }
}