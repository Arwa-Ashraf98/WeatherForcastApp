package com.example.weatherforecastapp.ui.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.data.repository.RepoInterface

class AlertViewModelFactory (private val repoInterface: RepoInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repoInterface) as T
        }else {
            throw IllegalArgumentException("Alert View model class cannot be found")
        }
    }
}