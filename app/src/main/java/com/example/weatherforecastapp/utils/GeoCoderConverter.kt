package com.example.weatherforecastapp.utils

import android.content.Context
import android.location.Geocoder
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import java.util.*

class GeoCoderConverter {

    companion object {
        fun getAddress(context: Context , lat : Double , long : Double): String? {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocation(
                lat,
                long,
                1
            )
            val stringBuffer = StringBuilder()


            if (addressList?.isNotEmpty() as Boolean){
                val address = addressList[0]
               for (i in 0 until address.maxAddressLineIndex){
                   stringBuffer.append(address.getAddressLine(i)).append("\n")
               }

                if (address.premises != null)
                    stringBuffer.append(address.premises).append(",\n ")
                stringBuffer.append(address.subAdminArea).append("\n")
                stringBuffer.append(address.locality).append(",\n ")
                stringBuffer.append(address.adminArea).append(",\n ")
                stringBuffer.append(address.countryName).append(",\n ")
                stringBuffer.append(address.postalCode)
            }

            return stringBuffer.toString()
        }
    }
}