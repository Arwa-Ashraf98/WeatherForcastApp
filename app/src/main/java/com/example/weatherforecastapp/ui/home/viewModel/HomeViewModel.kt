package com.example.weatherforecastapp.ui.home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.repository.RepoInterface
import com.example.weatherforecastapp.data.source.remote.APIState
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class HomeViewModel(private val repo: RepoInterface) : ViewModel() {
    val mutableStateFlow = MutableStateFlow<APIState>(APIState.Loading)
    private val error = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = error


    fun getWeatherDataOnline(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = repo.getWeatherDataOnline(
                lat = lat,
                long = long
            )
            flow.catch { exception ->
                mutableStateFlow.value = APIState.OnFail(exception)
            }.collect {
                if (it.isSuccessful) {
                    val data = it.body()
                    mutableStateFlow.value = APIState.OnSuccess(data!!)
                    repo.saveWeatherData(data)
                } else {
                    error.postValue(it.errorBody().toString())
                }
            }
        }
    }

    fun getWeatherDataLocally() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = repo.getWeatherDataLocally()
            flow.catch {
                mutableStateFlow.value = APIState.OnFail(it)
            }.collect {
                mutableStateFlow.value = APIState.OnSuccess(it)
            }
        }
    }


}