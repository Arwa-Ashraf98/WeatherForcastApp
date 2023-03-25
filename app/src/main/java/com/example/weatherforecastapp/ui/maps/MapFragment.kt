package com.example.weatherforecastapp.ui.maps

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.airbnb.lottie.model.Marker
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.databinding.FragmentMapBinding
import com.example.weatherforecastapp.utils.Constants
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback, MapsManagerInterface {

    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null
    private var binding: FragmentMapBinding? = null
    private var lat: Double? = 0.0
    private var long: Double? = 0.0
    private lateinit var mapManager: MapManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMap()
        mapManager = MapManager(requireContext(), this)


    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        mapManager.getLastLocation()
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun requestPermission() {
        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }





    override fun getLocationResult(locationResult: LocationResult?) {
        Log.e("TAG", "LAT ${locationResult?.lastLocation?.latitude}")
        Log.e("TAG", "LONG ${locationResult?.lastLocation?.longitude}")
        locationResult?.let {
            lat = it.lastLocation?.latitude
            long = it.lastLocation?.longitude
            setLocationOnMap(lat, long)
        } ?: throw NullPointerException("NULl")
    }

    private fun setLocationOnMap(lat: Double?, long: Double?) {
        val city = LatLng(lat!!, long!!)
        googleMap.addMarker(MarkerOptions().position(city).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(city))
    }

}