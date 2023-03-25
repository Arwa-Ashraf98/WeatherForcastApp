package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.models.ModelRoot
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {

    suspend fun getWeatherDataOnline(lat: Double, long: Double): Flow<Response<ModelRoot>>
    suspend fun saveWeatherData(modelRoot: ModelRoot)
    fun setLocalization(language: String)
    fun setWindSpeed(windSpeedChoice: String)
    fun setTemp(tempChoice: String)
    fun setLocation(locationChoice: String)
}