package com.example.weatherforecastapp.ui.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.repository.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsViewModel(private val repo: RepoInterface) : ViewModel() {

    fun setLanguage(language: String) {
        repo.setLocalization(language)

    }

    fun setWindSpeed(windSpeed: String) {
        repo.setWindSpeed(windSpeed)
    }

    fun setTemp(tempChoice: String) {
        repo.setTemp(tempChoice)
    }

    fun setAlarms(alarm: String) {
        repo.setAlarm(alarm)
    }


}