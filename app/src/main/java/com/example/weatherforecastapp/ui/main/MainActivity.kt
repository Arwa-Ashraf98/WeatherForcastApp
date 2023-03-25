package com.example.weatherforecastapp.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.databinding.ActivityMainBinding
import com.example.weatherforecastapp.ui.maps.MapManager
import com.example.weatherforecastapp.ui.maps.MapsManagerInterface
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.MyNetworkStatus
import com.example.weatherforecastapp.utils.MySnakeBar
import com.example.weatherforecastapp.utils.NetworkConnectivityObserver
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(), OnMapReadyCallback, MapsManagerInterface {
    private lateinit var snackBar: Snackbar
    private lateinit var googleMap: GoogleMap
    private var lat: Double? = 0.0
    private var long: Double? = 0.0
    private lateinit var mapManager: MapManager
    private lateinit var bNavigationView: BottomNavigationView
//    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        mapManager = MapManager(this, this)
        mapManager.getLastLocation()
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment_home)
        bNavigationView = findViewById(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bNavigationView, navController)
        setUpNavBottom(navController)
//        observeNetwork(binding.root)
    }

    private fun setUpNavBottom(navController: NavController) {
        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            if (navDestination.id == R.id.homeFragment) {
                bNavigationView.visibility = View.VISIBLE
            } else {
                bNavigationView.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        mapManager.getLastLocation()
    }


    override fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }


    override fun getLocationResult(locationResult: LocationResult?) {
        Log.e("ARWA", "LAT ${locationResult?.lastLocation?.latitude}")
        Log.e("ARWA", "LONG ${locationResult?.lastLocation?.longitude}")
        locationResult?.let {
            lat = it.lastLocation?.latitude
            long = it.lastLocation?.longitude
            MySharedPreferences.setLongitude(long)
            MySharedPreferences.setLatitude(lat)
        } ?: throw NullPointerException("NULl")
    }

//    private fun observeNetwork(view: View) {
//        NetworkConnectivityObserver.observeNetworkConnection().onEach {
//            if (it == MyNetworkStatus.Available) {
//                MySnakeBar.showSnakeBar(
//                    view,
//                    this,
//                    message = "network is $it",
//                    Snackbar.LENGTH_SHORT,
//                    R.color.black,
//                    R.color.white
//                ).dismiss()
//                Log.e("TAG", "network nw is $it")
//            } else {
//                MySnakeBar.showSnakeBar(
//                    view,
//                    this,
//                    message = "network is $it",
//                    Snackbar.LENGTH_SHORT,
//                    R.color.black,
//                    R.color.white
//                ).show()
//                Log.e("TAG", "network nw is $it")
//            }
//
//        }.launchIn(lifecycleScope)
//    }


}