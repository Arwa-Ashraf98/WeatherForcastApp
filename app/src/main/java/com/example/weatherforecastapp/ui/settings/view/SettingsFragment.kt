package com.example.weatherforecastapp.ui.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentSettingsBinding
import com.example.weatherforecastapp.ui.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapp.ui.settings.viewModel.SettingsViewModelFactory
import com.example.weatherforecastapp.utils.Constants


class SettingsFragment : Fragment() {
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsFactory: SettingsViewModelFactory
    private var _binding: FragmentSettingsBinding? = null
    private val binding = _binding
    private lateinit var tempRadioButton: RadioButton
    private lateinit var languageRadioButton: RadioButton
    private lateinit var locationRadioButton: RadioButton
    private lateinit var windSpeedRadioButton: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        onClicks(view)


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
                        settingsViewModel.setTemp(Constants.ARABIC)
                    }
                    getString(R.string.english) -> {
                        settingsViewModel.setLanguage(Constants.ENGLISH)
                        // set symbol
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

            // alert ask about it
            // TODO

            // location
            locationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                locationRadioButton = view.findViewById<View>(checkedId) as RadioButton

                when (locationRadioButton.text) {
                    getString(R.string.map) -> {
                        settingsViewModel.setLocation(Constants.MAP_LOCATION_VALUES)
                    }
                    getString(R.string.gps) -> {
                        settingsViewModel.setLocation(Constants.GPS_LOCATION_VALUES)
                    }
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}