package com.example.weatherforecastapp.ui.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.data.repository.RepoInterface
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModel


class SettingsViewModelFactory(private val repoInterface: RepoInterface) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(repoInterface) as T
        } else {
            throw IllegalArgumentException("View model class cannot be found")
        }
    }

}