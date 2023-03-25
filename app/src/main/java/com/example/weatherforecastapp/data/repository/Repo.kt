package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.source.local.LocalSourceInterface
import com.example.weatherforecastapp.data.source.remote.RemoteSourceInterface
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: RemoteSourceInterface,
    private val localSource: LocalSourceInterface
) : RepoInterface {

    companion object {
        @Volatile
        private var repo: Repo? = null
        fun getRepoInstance(
            remoteSource: RemoteSourceInterface,
            localSource: LocalSourceInterface
        ) = repo ?: synchronized(this) {
            val temp = Repo(remoteSource, localSource)
            this.repo = temp
            temp
        }
    }

    override suspend fun getWeatherDataOnline(
        lat: Double,
        long: Double
    ): Flow<Response<ModelRoot>> {
        return flowOf(remoteSource.getWeatherDataOnline(lat = lat, long = long))
    }

    override suspend fun saveWeatherData(modelRoot: ModelRoot) {
        return localSource.saveWeatherResponseAfterDelete(modelRoot)
    }

    override fun setLocalization(language: String) {
        MySharedPreferences.setLanguage(language)
    }

    override fun setWindSpeed(windSpeedChoice: String) {
        MySharedPreferences.setWindSpeed(windSpeedChoice)
    }

    override fun setTemp(tempChoice: String) {
        MySharedPreferences.setTemperature(tempChoice)
    }

    override fun setLocation(locationChoice: String) {
        MySharedPreferences.setLocation(locationChoice)
    }


}