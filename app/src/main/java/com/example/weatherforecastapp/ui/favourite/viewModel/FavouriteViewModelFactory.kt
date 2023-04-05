package com.example.weatherforecastapp.ui.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.data.repository.RepoInterface

class FavouriteViewModelFactory (private val repoInterface: RepoInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(repoInterface) as T
        } else {
            throw IllegalArgumentException("Fav View model class cannot be found")
        }
    }
}