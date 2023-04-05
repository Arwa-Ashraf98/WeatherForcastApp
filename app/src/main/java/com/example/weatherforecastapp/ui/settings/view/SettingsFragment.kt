package com.example.weatherforecastapp.ui.settings.view

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentSettingsBinding
import com.example.weatherforecastapp.ui.maps.utils.MapManager
import com.example.weatherforecastapp.ui.maps.utils.MapsManagerInterface
import com.example.weatherforecastapp.ui.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapp.ui.settings.viewModel.SettingsViewModelFactory
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.Extensions
import com.example.weatherforecastapp.utils.NetworkConnectivityObserver
import com.google.android.gms.location.LocationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

const val ID = 5005

class SettingsFragment : Fragment(), MapsManagerInterface {
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsFactory: SettingsViewModelFactory
    private var binding: FragmentSettingsBinding? = null
    private lateinit var tempRadioButton: RadioButton
    private lateinit var languageRadioButton: RadioButton
    private lateinit var locationRadioButton: RadioButton
    private lateinit var windSpeedRadioButton: RadioButton
    private lateinit var alarmRadioButton: RadioButton
    private lateinit var mapManager: MapManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        mapManager = MapManager(requireContext(), this)
        val location = MySharedPreferences.getLocation()
        val temp = MySharedPreferences.getTemperature()
        val windSpeed = MySharedPreferences.getWindSpeed()
        val language = MySharedPreferences.getLanguage()
        val alarm = MySharedPreferences.getAlarm()
        Log.e("ARWA", location as String )
        updateUi(
            location = location as String,
            temp = temp as String,
            windSpeed = windSpeed as String,
            language = language as String,
            alarm = alarm as String
        )
        onClicks(view)
        animateImage()
        getLanguageLocale()

    }

    private fun animateImage() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotation_animation)
        binding?.apply {
            imageViewSetting.startAnimation(animation)
        }
    }

    private fun updateUi(
        language: String,
        location: String,
        temp: String,
        windSpeed: String,
        alarm: String
    ) {
        // language
        if (language.equals(Constants.APP_LOCAL_EN_VALUES, true)) {
            binding?.languageRadioGroup?.check(binding?.englishRadioButton?.id as Int)
        } else {
            binding?.languageRadioGroup?.check(binding?.arabicRadioButton?.id as Int)
        }

        // location
        if (location.equals(Constants.GPS_LOCATION_VALUES, true)) {
            binding?.locationRadioGroup?.check(binding?.gpsRadioButton?.id!!)
        } else {
            binding?.locationRadioGroup?.check(binding?.mapRadioButton?.id!!)
        }


        // temperature
        if (temp.equals(Constants.TEMP_KELVIN_VALUES, true)) {
            binding?.tempRadioGroup?.check(binding?.kelvinRadioButton?.id!!)
        } else if (temp.equals(Constants.TEMP_FAHRENHEIT_VALUES, true)) {
            binding?.tempRadioGroup?.check(binding?.fahrenheitRadioButton?.id!!)
        } else {
            binding?.tempRadioGroup?.check(binding?.celsiusRadioButton?.id!!)
        }

        // windSpeed
        if (windSpeed.equals(Constants.WIND_SPEED_METER_SEC_VALUES, true)) {
            binding?.windSpeedRadioGroup?.check(binding?.meterRadioButton?.id!!)
        } else {
            binding?.apply {
                windSpeedRadioGroup.check(binding?.mileRadioButton?.id!!)
            }
        }

        // alarm
        if (alarm.equals(Constants.ALARM, true)) {
            binding?.apply {
                alarmRadioGroup.check(alarmsRadioButton.id)
            }
        } else {
            binding?.apply {
                alarmRadioGroup.check(notificationRadioButton.id)
            }
        }
    }

    private fun initViewModel() {
        settingsFactory = SettingsViewModelFactory(
            Repo.getRepoInstance(
                RemoteSource.getRemoteSourceInstance(),
                LocalSource.getLocalSourceInstance()
            )
        )
        settingsViewModel =
            ViewModelProvider(requireActivity(), settingsFactory)[SettingsViewModel::class.java]
    }

    private fun onClicks(view: View) {
        binding?.apply {
            // language
            languageRadioGroup.setOnCheckedChangeListener { _, checkId ->
                languageRadioButton = view.findViewById<View>(checkId) as RadioButton
                when (languageRadioButton.text) {
                    getString(R.string.arabic) -> {
                        settingsViewModel.setLanguage(Constants.APP_LOCAL_AR_VALUES)
                        changeLanguageLocaleTo(Constants.APP_LOCAL_AR_VALUES)
                    }
                    getString(R.string.english) -> {
                        settingsViewModel.setLanguage(Constants.APP_LOCAL_EN_VALUES)
                        changeLanguageLocaleTo(Constants.APP_LOCAL_EN_VALUES)
                    }
                }
            }

            // windSpeed
            windSpeedRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                windSpeedRadioButton = view.findViewById<View>(checkedId) as RadioButton

                when (windSpeedRadioButton.text) {
                    getString(R.string.mile_hour) -> {
                        settingsViewModel.setWindSpeed(Constants.WIND_SPEED_MILE_HOUR_VALUES)
                    }
                    getString(R.string.meter_sec) -> {
                        settingsViewModel.setWindSpeed(Constants.WIND_SPEED_METER_SEC_VALUES)
                    }
                }
            }

            // temperature
            tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                tempRadioButton = view.findViewById<View>(checkedId) as RadioButton

                when (tempRadioButton.text) {
                    getString(R.string.kelvin) -> {
                        settingsViewModel.setTemp(Constants.TEMP_KELVIN_VALUES)
                    }

                    getString(R.string.celsius) -> {
                        settingsViewModel.setTemp(Constants.TEMP_CELSIUS_VALUES)
                    }

                    getString(R.string.fahrenheit) -> {
                        settingsViewModel.setTemp(Constants.TEMP_FAHRENHEIT_VALUES)
                    }
                }

            }

            // alarm
            alarmRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                alarmRadioButton = view.findViewById<View>(checkedId) as RadioButton
                when (alarmRadioButton.text) {
                    getString(R.string.alarm) -> {
                        checkPermissionOfOverlay()

                        settingsViewModel.setAlarms(Constants.ALARM)
                    }

                    getString(R.string.notification) -> {
                        settingsViewModel.setAlarms(Constants.NOTIFICATION)
                    }
                }
            }

            // location
            locationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                locationRadioButton = view.findViewById<View>(checkedId) as RadioButton

                when (locationRadioButton.text) {
                    getString(R.string.map) -> {
                        if (NetworkConnectivityObserver.isOnline(requireContext())) {
                            val action =
                                SettingsFragmentDirections.actionSettingsFragmentToMapFragment("settings")
                            findNavController().navigate(action)
                        } else {
                            Extensions.showIonfirmationDialog(
                                requireContext(),
                                "OOPS there is No network Please Turn on WIFI and try later"
                            )
                        }
                    }
                    getString(R.string.gps) -> {
                        if (NetworkConnectivityObserver.isOnline(requireContext())) {
                            Log.e("ARWA", "onClicks: GPS is Clicked", )
                            mapManager.getLastLocation()
                        } else {
                            Extensions.showIonfirmationDialog(
                                requireContext(),
                                getString(R.string.no_intenrnet)
                            )
                        }

                    }
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "granted", Toast.LENGTH_SHORT).show()
//                mapManager.getLastLocation()
            } else {
                Toast.makeText(requireContext(), "no no granted", Toast.LENGTH_SHORT).show()

            }
        } else {
            Toast.makeText(requireContext(), "no granted", Toast.LENGTH_SHORT).show()

        }
    }


    private fun changeLanguageLocaleTo(lan: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
// Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale)
// AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun getLanguageLocale(): String {
        return AppCompatDelegate.getApplicationLocales().toLanguageTags()
    }


    private fun checkPermissionOfOverlay() {
        // Check if we already  have permission
        if (!Settings.canDrawOverlays(requireContext())) {

            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle("Display on top")
                .setMessage("You Should let us to draw on top")
                .setPositiveButton("Okay") { dialog: DialogInterface, _: Int ->

                    // Navigate to Manage Overlay settings in device
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().applicationContext.packageName)
                    )
                    startActivityForResult(intent, 1)
                    dialog.dismiss()

                }.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }.show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }


    override fun getLocationResult(locationResult: LocationResult?) {
        locationResult?.let {
            Log.e("ARWA", "getLocationResult: inside SETTINGS ", )
            MySharedPreferences.setLatitude(it.lastLocation?.latitude)
            MySharedPreferences.setLongitude(it.lastLocation?.longitude)
            MySharedPreferences.setLocation(Constants.GPS_LOCATION_VALUES)
            Log.e("ARWA", "00000"+MySharedPreferences.getLocation().toString() )
            Log.e("ARWA", "00000"+MySharedPreferences.getLatitude().toString() )
        } ?: throw NullPointerException("NULl")
    }

}