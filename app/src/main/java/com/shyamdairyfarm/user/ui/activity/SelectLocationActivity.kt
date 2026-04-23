package com.shyamdairyfarm.user.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.repository.GeocoderRepository
import com.shyamdairyfarm.user.databinding.ActivitySelectLocationBinding
import com.shyamdairyfarm.user.helper.LocationManager
import com.shyamdairyfarm.user.ui.viewmodel.SelectLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: SelectLocationViewModel

    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = GeocoderRepository(this)
        viewModel = SelectLocationViewModel(repository)

        setupMap()
        observeViewModel()

        locationManager = LocationManager(
            activity = this,
            onLocationStatusChanged = { isLoading ->
                binding.llLoadingLocation.visibility = if (isLoading) View.VISIBLE else View.GONE
            },
            onLocationReceived = { latLng ->
                if (::mMap.isInitialized) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    )
                }
            }
        )

        binding.fabMyLocation.setOnClickListener {
            checkPermissionAndRequestLocation()
        }

        binding.btnConfirm.setOnClickListener {
            selectedLatLng?.let {
                val resultIntent = Intent().apply {
                    putExtra("address", binding.tvSelectedAddress.text.toString())
                    putExtra("lat", it.latitude)
                    putExtra("lng", it.longitude)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.address.observe(this) {
            binding.tvSelectedAddress.text = it
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
            binding.tvLatLng.visibility = View.GONE
        }

        mMap.setOnCameraIdleListener {
            selectedLatLng = mMap.cameraPosition.target
            selectedLatLng?.let {
                viewModel.fetchAddress(it)
                binding.tvLatLng.text = "${String.format("%.6f", it.latitude)}, ${String.format("%.6f", it.longitude)}"
                binding.tvLatLng.visibility = View.VISIBLE
            }
        }
    }

    private fun checkPermissionAndRequestLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
                locationManager.reset()
                locationManager.checkLocationSettings(2001)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs location access to find your delivery address automatically. Please grant the permission.")
            .setPositiveButton("Try Again") { _, _ ->
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Permanently Denied")
            .setMessage("You have denied location permission multiple times. Please enable it in the app settings to use this feature.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        try {
            mMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001) {
            if (resultCode == Activity.RESULT_OK) {
                locationManager.requestLocation()
            } else {
                Toast.makeText(this, "Location services are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates()
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                enableMyLocation()
                locationManager.checkLocationSettings(2001)
            } else {
                // If it's not granted and we shouldn't show rationale, it means it's permanently denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showSettingsDialog()
                } else {
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
}
