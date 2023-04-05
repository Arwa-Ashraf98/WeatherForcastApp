package com.example.weatherforecastapp.ui.favourite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.repository.RepoInterface
import com.example.weatherforecastapp.data.source.local.LocalState
import com.example.weatherforecastapp.data.source.remote.APIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repo: RepoInterface) : ViewModel() {

    val mutableStateFlow = MutableStateFlow<LocalState>(LocalState.Loading)


    fun getFavAddresses(){
        viewModelScope.launch {
            val flow = repo.getFavPlaces()
            flow.catch {
                mutableStateFlow.value = LocalState.OnFail(it)
            }.collect{
                val data = it
                mutableStateFlow.value = LocalState.OnSuccess(data)

            }
        }
    }

    fun deletePlace(favAddress: FavAddress){
        viewModelScope.launch {
            repo.deleteFavPlace(favAddress)
        }
        getFavAddresses()
    }


}