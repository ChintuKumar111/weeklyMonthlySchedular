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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.repository.GeocoderRepository
import com.shyamdairyfarm.user.helper.LocationManager
import com.shyamdairyfarm.user.ui.activity.HomeActivity
import com.shyamdairyfarm.user.ui.activity.utils.DialogUtils
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import com.shyamdairyfarm.user.ui.viewmodel.SelectLocationViewModel
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SignUpAddressMapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var selectedLatLng: LatLng? = null

    private val viewModel: AuthViewModel by sharedViewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sign_up_address_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnRegister = view.findViewById(R.id.btnRegister)
        progressBar = view.findViewById(R.id.progressBar)
        setupMap()

        locationManager = LocationManager(
            activity = requireActivity(),
            onLocationStatusChanged = { isLoading ->
                view.findViewById<View>(R.id.llLoadingLocation).visibility =
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

        observeViewModel(view)
        setupButtonClick(view)

        view.findViewById<View>(R.id.fabMyLocation).setOnClickListener {
            checkPermissionAndRequestLocation()
        }
    }

    private fun observeViewModel(view: View) {
        viewModel.address.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.tvSelectedAddress).text = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newCustomerSignUpState.collectLatest { state ->
                when (state) {
                    is UiState.Error -> {
                        setLoading(false)

                        DialogUtils.showErrorDialog(requireContext(), msg = state.message){
                            viewModel.logout()
                        }
                    }

                    UiState.Idle -> {
                        setLoading(false)
                    }

                    UiState.Loading -> {
                        setLoading(true) // 👈 SHOW LOADER HERE
                    }

                    is UiState.Success<*> -> {
                        setLoading(false)

                        val intent = Intent(context, HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    is UiState.ExpiredToken -> {}
                    is UiState.UnauthorizedAccess -> {
                        // show logout button for logout
                    }
                }
            }
        }
    }

    private fun setupButtonClick(view: View){
        view.findViewById<MaterialButton>(R.id.btnRegister).setOnClickListener {
            viewModel.registerNewCustomer()
        }

    }
    fun setLoading(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading

        if (isLoading) {
            btnRegister.text = ""
            progressBar.visibility = View.VISIBLE
            btnRegister.alpha = 0.7f
        } else {
            btnRegister.text = "Confirm Location"
            progressBar.visibility = View.GONE
            btnRegister.alpha = 1f
        }
    }
    // ✅ MAP READY
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
            view?.findViewById<TextView>(R.id.tvSelectedAddress)?.text = "Fetching address..."
        }

        mMap.setOnCameraIdleListener {
            selectedLatLng = mMap.cameraPosition.target

            selectedLatLng?.let {
                viewModel.fetchAddress(it)

                viewModel.updateLatLang(it.latitude, it.longitude)

                view?.findViewById<TextView>(R.id.tvLatLng)?.apply {
                    text = "${it.latitude}, ${it.longitude}"
                    visibility = View.VISIBLE
                }
            }
        }
    }

    // ✅ MAP SETUP (IMPORTANT CHANGE)
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

    // ✅ PERMISSION
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
    }
}