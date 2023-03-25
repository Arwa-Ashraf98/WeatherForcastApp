package com.example.weatherforecastapp.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.repository.RepoInterface
import com.example.weatherforecastapp.data.source.remote.APIState
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response


class HomeViewModel(private val repo: RepoInterface) : ViewModel() {
    val mutableStateFlow = MutableStateFlow<APIState>(APIState.Loading)
    val mutableStateFlowLocal = MutableStateFlow<APIState>(APIState.Loading)
    private val error = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = error

    init {
        getProductOnline()
    }

    private fun getProductOnline() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = repo.getWeatherDataOnline(
                lat = MySharedPreferences.getLatitude() as Double,
                long = MySharedPreferences.getLongitude() as Double
            )
            flow.catch { exception ->
                mutableStateFlow.value = APIState.OnFail(exception)
            }.collect {
                if (it.isSuccessful) {
                    val data = it.body()
                    repo.saveWeatherData(data as ModelRoot)
                    mutableStateFlow.value = APIState.OnSuccess(data)
                } else {



                    error.postValue(it.errorBody().toString())
                }
            }
        }
    }


}