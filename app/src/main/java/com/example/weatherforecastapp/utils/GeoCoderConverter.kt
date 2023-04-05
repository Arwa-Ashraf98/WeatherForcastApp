package com.example.weatherforecastapp.utils

import android.content.Context
import android.location.Geocoder
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import java.util.*

class GeoCoderConverter {

    companion object {
        fun getAddress(context: Context, lat: Double, long: Double): String? {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocation(
                lat,
                long,
                1
            )
            val stringBuffer = StringBuilder()


            if (addressList?.isNotEmpty() as Boolean) {
                val address = addressList[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    stringBuffer.append(address.getAddressLine(i)).append("\n")
                }

                if (address.premises != null)
                    stringBuffer.append(address.premises).append(",\n ")
                if (address.subAdminArea != null)
                    stringBuffer.append(address.subAdminArea).append("\n")
                if (address.locality != null)
                    stringBuffer.append(address.locality)
                if (address.adminArea != null)
                    stringBuffer.append(address.adminArea).append(",\n ")
                if (address.countryName != null)
                    stringBuffer.append(address.countryName).append(",\n ")
                if (address.postalCode != null)
                    stringBuffer.append(address.postalCode)
            }

            return stringBuffer.toString()
        }
    }
}