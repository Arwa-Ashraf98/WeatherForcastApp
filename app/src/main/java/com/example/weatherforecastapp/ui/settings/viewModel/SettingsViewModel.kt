package com.example.weatherforecastapp.ui.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.repository.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: RepoInterface) : ViewModel() {

    fun setLanguage(language: String) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.setLocalization(language)
        }
    }

    fun setWindSpeed(windSpeed: String) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.setWindSpeed(windSpeed)
        }
    }

    fun setTemp(tempChoice: String) {
        viewModelScope.launch(Dispatchers.Main)
        {
            repo.setTemp(tempChoice)
        }
    }

    fun setLocation(locationChoice: String) {
        viewModelScope.launch(Dispatchers.Main)
        {
            repo.setTemp(locationChoice)
        }
    }



}