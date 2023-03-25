package com.example.weatherforecastapp.ui.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.Current
import com.example.weatherforecastapp.data.models.Daily
import com.example.weatherforecastapp.data.models.Hourly
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.remote.APIState
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.databinding.FragmentHomeBinding
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModel
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModelFactory
import com.example.weatherforecastapp.utils.HandleIcon
import com.example.weatherforecastapp.utils.MySnakeBar
import com.example.weatherforecastapp.utils.Converters
import com.example.weatherforecastapp.utils.GeoCoderConverter
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ARWA", MySharedPreferences.getLatitude().toString())
        Log.e("ARWA", MySharedPreferences.getLongitude().toString())
        Toast.makeText(
            requireContext(),
            MySharedPreferences.getLongitude().toString(),
            Toast.LENGTH_SHORT
        ).show()
        Toast.makeText(
            requireContext(),
            MySharedPreferences.getLatitude().toString(),
            Toast.LENGTH_SHORT
        ).show()
        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()
        initViewModel()
        observeData()


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

    private fun observeData() {
        observeErrors()
        lifecycleScope.launchWhenCreated {
            homeViewModel.mutableStateFlow.collect {
                when (it) {
                    is APIState.OnSuccess -> {
                        val currentObject = it.weatherData.current
                        inflateData(it.weatherData, currentObject)

                    }
                    is APIState.Loading -> {
                        // progress have to shown here
                    }
                    else -> {
                        // show message for error
                        Toast.makeText(
                            requireContext(),
                            "Check your connection",
                            Toast.LENGTH_SHORT
                        ).show()

                        // show snake bar
                        MySnakeBar.showSnakeBar(
                            requireView(),
                            requireContext(),
                            "Check Connection",
                            Snackbar.LENGTH_SHORT,
                            R.color.white,
                            R.color.colorPrimaryDarkNight
                        )
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

    private fun inflateData(weatherData: ModelRoot, current: Current) {
        binding?.apply {
            textViewHumidity.text = current.humidity.toString()
            textViewTemperatureHome.text =
                Converters.convertTemperature(current.temp, requireContext())
            textViewCity.text =
                GeoCoderConverter.getAddress(requireContext(), weatherData.lat, weatherData.lon)
            textViewDate.text =
                Converters.convertTimestampToString(
                    weatherData.current.dt,
                    Converters.DATETIME_PATTERN
                )
            textViewWeatherDescription.text =
                weatherData.current.weather[0].description
            textViewWindSpeed.text = weatherData.current.wind_speed.toString()
            imageWeatherHome.setImageResource(
                HandleIcon.getIcon(
                    weatherData.current.weather[0].icon
                )
            )

            // recycler hours
            setHourlyList(weatherData.hourly)

            // recycler Days
        }
        setDaysList(weatherData.daily)
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
}