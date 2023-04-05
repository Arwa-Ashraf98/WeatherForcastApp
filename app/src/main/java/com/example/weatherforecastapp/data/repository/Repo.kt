package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.source.local.LocalSourceInterface
import com.example.weatherforecastapp.data.source.remote.RemoteSourceInterface
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun getLocalization(): String? {
        return MySharedPreferences.getLanguage()
    }

    override fun getTemp(): String? {
        return MySharedPreferences.getTemperature()
    }

    override fun getWindSpeed(): String? {
        return MySharedPreferences.getWindSpeed()
    }

    override fun getLocation(): String? {
        return MySharedPreferences.getLocation()
    }

    override fun setAlarm(alarm: String) {
        MySharedPreferences.setAlarm(alarm)
    }

    override fun getAlarm(): String {
        return MySharedPreferences.getAlarm() as String
    }


    override suspend fun insertFaveLocation(favLocation: FavAddress) {
        localSource.insertFavLocation(favLocation)
    }

    override suspend fun getFavPlaces(): Flow<List<FavAddress>> {
        return flowOf(localSource.getFavPlaces())
    }

    override suspend fun deleteFavPlace(favAddress: FavAddress) {
        return localSource.deleteFavPlace(favAddress)
    }

    override suspend fun getWeatherDataLocally(): Flow<ModelRoot> {
        return flowOf(localSource.getWeatherDataLocally())
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long {
        return localSource.insertAlert(alertEntity)
    }

    override suspend fun getAlertById(id: Int?): Flow<AlertEntity> {
        return flowOf(localSource.getAlertById(id ?: 0))
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        return localSource.deleteAlert(alertEntity)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertEntity>> {
        return flowOf(localSource.getAllAlerts())
    }
}