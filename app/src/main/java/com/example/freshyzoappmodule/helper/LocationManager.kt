package com.example.freshyzoappmodule.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

class LocationManager(
    private val activity: Activity,
    private val onLocationStatusChanged: ((Boolean) -> Unit)? = null,
    private val onLocationReceived: (LatLng) -> Unit
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private val settingsClient =
        LocationServices.getSettingsClient(activity)

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .setWaitForAccurateLocation(true)
            .build()

    private var isFirstUpdate = true
    private val cancellationTokenSource = CancellationTokenSource()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            if (isFirstUpdate) {
                isFirstUpdate = false
                onLocationStatusChanged?.invoke(false)
                onLocationReceived(LatLng(location.latitude, location.longitude))
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    fun checkLocationSettings(requestCode: Int) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                requestLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(activity, requestCode)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        onLocationStatusChanged?.invoke(true)
        
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null && isFirstUpdate) {
                isFirstUpdate = false
                onLocationStatusChanged?.invoke(false)
                onLocationReceived(LatLng(location.latitude, location.longitude))
            } else {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun removeUpdates() {
        cancellationTokenSource.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun reset() {
        isFirstUpdate = true
    }
}
