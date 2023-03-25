package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.models.ModelRoot
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {

    suspend fun getWeatherDataOnline(lat: Double, long: Double): Flow<Response<ModelRoot>>
    suspend fun saveWeatherData(modelRoot: ModelRoot)
    suspend fun setLocalization(language : String)
    suspend fun setWindSpeed(windSpeedChoice : String)
    suspend fun setTemp(tempChoice : String)
    suspend fun setLocation(locationChoice : String)
}