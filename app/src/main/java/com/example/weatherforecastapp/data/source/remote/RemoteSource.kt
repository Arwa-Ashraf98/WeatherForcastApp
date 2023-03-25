package com.example.weatherforecastapp.data.source.remote

import com.example.weatherforecastapp.data.models.ModelRoot
import retrofit2.Response

class RemoteSource private constructor() : RemoteSourceInterface {
    companion object {
        @Volatile
        private var remoteSourceInstance: RemoteSource? = null

        fun getRemoteSourceInstance() = remoteSourceInstance ?: synchronized(this) {
            val temp = RemoteSource()
            remoteSourceInstance = temp
            temp
        }
    }

    override suspend fun getWeatherDataOnline(lat: Double, long: Double): Response<ModelRoot> {
        return RetrofitConnection.getServices().getWeatherData(lat = lat , lon = long)
    }

}