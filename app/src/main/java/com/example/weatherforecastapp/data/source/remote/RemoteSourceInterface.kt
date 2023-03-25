package com.example.weatherforecastapp.data.source.remote

import com.example.weatherforecastapp.data.models.ModelRoot
import retrofit2.Response

interface RemoteSourceInterface {

    suspend fun getWeatherDataOnline(lat : Double , long : Double) : Response<ModelRoot>
}