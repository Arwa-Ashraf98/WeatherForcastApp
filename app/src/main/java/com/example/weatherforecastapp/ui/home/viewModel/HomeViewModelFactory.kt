package com.example.weatherforecastapp.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.data.repository.RepoInterface

class HomeViewModelFactory (private val repoInterface: RepoInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repoInterface) as T
        } else {
            throw IllegalArgumentException("View model class cannot be found")
        }
    }
}