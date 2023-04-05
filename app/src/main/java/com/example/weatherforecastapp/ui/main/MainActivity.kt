package com.example.weatherforecastapp.ui.main

import android.Manifest
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.ui.maps.utils.MapManager
import com.example.weatherforecastapp.ui.maps.utils.MapsManagerInterface
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.MyNetworkStatus
import com.example.weatherforecastapp.utils.NetworkConnectivityObserver
import com.google.android.gms.cast.framework.SessionManager
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var bNavigationView: BottomNavigationView
    private lateinit var textView: TextView


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.noInterNetTextView)
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment_home)
        bNavigationView = findViewById(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bNavigationView, navController)
        setUpNavBottom(navController)
        setLan(MySharedPreferences.getLanguage() as String)
        observeNetwork()
    }

    private fun setUpNavBottom(navController: NavController) {
        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            if (navDestination.id == R.id.homeFragment
            ) {
                bNavigationView.visibility = View.VISIBLE
            } else {
                bNavigationView.visibility = View.GONE
            }
        }
    }



    private fun observeNetwork() {
        NetworkConnectivityObserver.observeNetworkConnection().onEach {
            if (it == MyNetworkStatus.Available) {
                Log.e("MAIN0", "network nw is $it")
                textView.visibility = View.GONE
            } else {


                textView.visibility = View.VISIBLE
                Log.e("MAIN0", "network nw is $it")
            }
        }.launchIn(lifecycleScope)
    }

    private fun setLan(language: String) {
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        Locale.setDefault(Locale(language))

        configuration.setLayoutDirection(Locale(language))
        // update configuration
        resources.updateConfiguration(configuration, metric)
        // notify configuration
        onConfigurationChanged(configuration)

    }


}