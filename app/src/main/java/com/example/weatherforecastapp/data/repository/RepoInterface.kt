package com.example.weatherforecastapp.data.repository

import android.graphics.ColorSpace.Model
import android.media.MediaSession2Service.MediaNotification
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {

    suspend fun getWeatherDataOnline(lat: Double, long: Double): Flow<Response<ModelRoot>>
    suspend fun saveWeatherData(modelRoot: ModelRoot)


    // shared p
    fun setLocalization(language: String)
    fun setWindSpeed(windSpeedChoice: String)
    fun setTemp(tempChoice: String)
    fun setLocation(locationChoice: String)
    fun setAlarm(alarm: String)
    fun getLocalization() : String?
    fun getTemp() : String?
    fun getWindSpeed() : String?
    fun getLocation() : String?

    fun getAlarm() : String?

    // favPlaces
    suspend fun insertFaveLocation(favLocation: FavAddress)
    suspend fun getFavPlaces(): Flow<List<FavAddress>>
    suspend fun deleteFavPlace(favAddress: FavAddress)
    suspend fun getWeatherDataLocally(): Flow<ModelRoot>


    // alerts
    suspend fun insertAlert(alertEntity: AlertEntity): Long
    suspend fun getAlertById(id: Int?): Flow<AlertEntity>
    suspend fun deleteAlert(alertEntity: AlertEntity)
    suspend fun getAllAlerts(): Flow<List<AlertEntity>>
}