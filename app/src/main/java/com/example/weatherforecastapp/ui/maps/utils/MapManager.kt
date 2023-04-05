package com.example.weatherforecastapp.ui.maps.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*

class MapManager(
    private val context: Context,
    private val mapsManagerInterface: MapsManagerInterface
)  {
    private lateinit var myFusedLocationProviderClient: FusedLocationProviderClient



    fun getLastLocation() {
        Log.e("MAP", "getLastLocation: is Called", )
        if (checkPermission()) {
            Log.e("MAP", "getLastLocation: check permission is true ", )
            if (isLocationEnabled()) {
                Log.e("MAP", "getLastLocation: check permission is true ", )
                requestNewLocationData()
            } else {
                Toast.makeText(context, "Turn on The Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            // i did this because it require activity and i only had context
            mapsManagerInterface.requestPermission()
        }

    }


    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private
    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.e("MAP", "requestNewLocationData: ", )
        val myLocationRequest = LocationRequest()

        myLocationRequest.priority = LocationRequest.QUALITY_BALANCED_POWER_ACCURACY

        myLocationRequest.interval = 0

        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        myFusedLocationProviderClient.requestLocationUpdates(
            myLocationRequest, myLocationCall, Looper.myLooper()
        )
    }

    private val myLocationCall: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.e("MAP", "onLocationResult: ${locationResult.locations[0]}", )
            myFusedLocationProviderClient.removeLocationUpdates(this)
            mapsManagerInterface.getLocationResult(locationResult)
        }
    }
}