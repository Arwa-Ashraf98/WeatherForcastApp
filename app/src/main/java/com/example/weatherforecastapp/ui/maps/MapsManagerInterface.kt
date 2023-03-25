package com.example.weatherforecastapp.ui.maps

import com.google.android.gms.location.LocationResult

interface MapsManagerInterface {

    fun requestPermission()

    fun getLocationResult(locationResult: LocationResult?)


}