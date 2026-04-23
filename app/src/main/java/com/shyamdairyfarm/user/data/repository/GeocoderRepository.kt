package com.shyamdairyfarm.user.data.repository

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class GeocoderRepository(private val context: Context) {

    suspend fun getAddress(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addressList.isNullOrEmpty()) {
                addressList[0].getAddressLine(0)
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            "Unable to fetch address"
        }
    }
}