package com.example.weatherforecastapp.ui.home.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.APIState
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentHomeBinding
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModel
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModelFactory
import com.example.weatherforecastapp.ui.maps.utils.MapManager
import com.example.weatherforecastapp.ui.maps.utils.MapsManagerInterface
import com.example.weatherforecastapp.utils.*
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : Fragment(), MapsManagerInterface {

    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private var comeFrom: Boolean = false
    private var lat: Double? = null
    private var long: Double? = null
    private lateinit var mapManager: MapManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        mapManager = MapManager(requireContext(), this)
        if (MySharedPreferences.getLatitude() == 0.0 && MySharedPreferences.getLongitude() == 0.0) {
            mapManager.getLastLocation()
            getData(NetworkConnectivityObserver.isOnline(requireContext()))
        } else {
            val isOnline = NetworkConnectivityObserver.isOnline(requireContext())
            getData(isOnline)
        }

        // ----------------------------------------------------------------
        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()
        // -----------------------------------------------------------------

        comeFrom = arguments?.getBoolean("fav", false) as Boolean
        lat = arguments?.getDouble(Constants.LATITUDE, 0.0) as Double
        long = arguments?.getDouble(Constants.LONGITUDE, 0.0) as Double

        // --------------------------------------------------------------
        observeDataOnline()
        swipeToRefresh()

    }

    private fun getData(isOnline: Boolean) {
        if (isOnline) {
            comingFrom()
        } else {
            homeViewModel.getWeatherDataLocally()
        }
    }

    private fun swipeToRefresh() {
        binding?.swipeRefreshHome?.setOnRefreshListener {
            if (NetworkConnectivityObserver.isOnline(requireContext())) {
                comingFrom()
            } else {
                homeViewModel.getWeatherDataLocally()
            }
            observeDataOnline()
            binding?.swipeRefreshHome?.isRefreshing = false
        }
    }

    private fun comingFrom() {
        if (comeFrom) {
            homeViewModel.getWeatherDataOnline(lat as Double, long as Double)

        }
        else {
            homeViewModel.getWeatherDataOnline(
                lat = MySharedPreferences.getLatitude() as Double,
                long = MySharedPreferences.getLongitude() as Double
            )

        }
    }

    override fun onStop() {
        super.onStop()
        comeFrom = false
    }

    private fun initViewModel() {
        val myViewModelFactory = HomeViewModelFactory(
            Repo.getRepoInstance(
                RemoteSource.getRemoteSourceInstance(),
                LocalSource.getLocalSourceInstance(),
            )
        )
        homeViewModel =
            ViewModelProvider(requireActivity(), myViewModelFactory)[HomeViewModel::class.java]
    }

    private fun observeDataOnline() {
        observeErrors()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.mutableStateFlow.collect {
                    when (it) {
                        is APIState.OnSuccess -> {

                            val currentObject = it.weatherData.current
                            binding?.apply {
                                progressBarHome.visibility = View.GONE
                                swipeRefreshHome.visibility = View.VISIBLE
                            }
                            inflateUI(current = currentObject, weatherData = it.weatherData)
                        }
                        is APIState.Loading -> {
                            // progress have to shown here
                            binding?.apply {
                                progressBarHome.visibility = View.VISIBLE
                                swipeRefreshHome.visibility = View.GONE
                            }
                        }
                        is APIState.OnFail -> {
                            // show message for error
                            binding?.apply {
                                progressBarHome.visibility = View.GONE
                                swipeRefreshHome.visibility = View.VISIBLE
                            }
                            Toast.makeText(
                                requireContext(),
                                "Check ${it.errorMessage}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
            }
        }
    }


    private fun observeErrors() {
        homeViewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun inflateUI(weatherData: ModelRoot, current: Current) {
        binding?.apply {
            textViewHumidity.text = Converters.convertHumidityOrPressure(current.humidity).plus("%")
            textViewTemperatureHome.text =
                Converters.convertTemperature(current.temp, requireContext())
            textViewCity.text =
                GeoCoderConverter.getAddress(requireContext(), weatherData.lat, weatherData.lon)
            textViewDate.text =
                Converters.convertTimestampToString(
                    weatherData.current.dt.toLong(),
                    Converters.DAY_PATTERN_DAY
                )
            textViewWeatherDescription.text =
                weatherData.current.weather[0].description
            textViewWindSpeed.text =
                Converters.convertWindFromMeterPerSecondToMileOerHour(
                    weatherData.current.wind_speed,
                    requireContext()
                )
            Glide
                .with(requireContext())
                .load(Constants.WEATHER_IMAGE_BASE_URL + weatherData.current.weather[0].icon + ".png")
                .placeholder(R.drawable.loading)
                .error(R.drawable.cloudy)
                .into(imageWeatherHome)


            textViewFeelsLike.text = Converters.convertHumidityOrPressure(current.feels_like)
            textViewCardClouds.text = Converters.convertHumidityOrPressure(current.clouds).plus("%")
            textViewPressure.text = Converters.convertHumidityOrPressure(current.pressure).plus("%")
            textViewWindSpeed.text = Converters.convertWindFromMeterPerSecondToMileOerHour(
                current.wind_speed,
                requireContext()
            )

            // recycler hours
            setHourlyList(weatherData.hourly.take(24))

            // recycler Days
        }
        setDaysList(weatherData.daily.take(7))
    }

    private fun setHourlyList(list: List<Hourly>) {
        hourlyAdapter.setList(list)
        binding?.recyclerHours?.adapter = hourlyAdapter
    }

    private fun setDaysList(list: List<Daily>) {
        dailyAdapter.setDailyList(list)
        binding?.recyclerDays?.adapter = dailyAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT) {
            Log.e("IF", "onRequestPermissionsResult: 0 ", )
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("IF", "onRequestPermissionsResult: 2", )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.e("IF", "onRequestPermissionsResult: 3", )
                    mapManager.getLastLocation()
                }
            }
        }
    }

    override fun getLocationResult(locationResult: LocationResult?) {
        locationResult?.let {
            MySharedPreferences.setLongitude(locationResult.lastLocation?.longitude)
            MySharedPreferences.setLatitude(locationResult.lastLocation?.latitude)
            MySharedPreferences.setLocation(Constants.GPS_LOCATION_VALUES)
        } ?: throw NullPointerException("NULl")
    }
}