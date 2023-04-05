package com.example.weatherforecastapp.ui.maps.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.repository.RepoInterface
import kotlinx.coroutines.launch

class MapViewModel(private val repo: RepoInterface) : ViewModel() {

    fun insertFavLocation(favLocation: FavAddress) {
        viewModelScope.launch {
            repo.insertFaveLocation(favLocation)
        }
    }
}