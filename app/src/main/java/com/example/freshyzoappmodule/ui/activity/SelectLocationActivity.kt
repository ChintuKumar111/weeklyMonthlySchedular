package com.example.freshyzoappmodule.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivitySelectLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnConfirm.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("address", binding.tvSelectedAddress.text.toString())
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        val defaultLocation = LatLng(21.2514, 81.6296) // Raipur example
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16f))
//
//        mMap.setOnCameraIdleListener {
//            selectedLatLng = mMap.cameraPosition.target
//            selectedLatLng?.let {
//                getAddressFromLatLng(it)
//            }
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f)
                )
            }
        }

        mMap.setOnCameraIdleListener {
            selectedLatLng = mMap.cameraPosition.target
            selectedLatLng?.let {
                getAddressFromLatLng(it)
            }
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addressList = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            if (!addressList.isNullOrEmpty()) {
                binding.tvSelectedAddress.text =
                    addressList[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
