package com.example.weatherforecastapp.data.source.local

import androidx.room.*
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.Alerts
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponse(modelRoot: ModelRoot): Long

    @Query("SELECT * from ModelRootTable LIMIT 1")
    fun getWeatherResponseFromLocal(): ModelRoot?

    @Delete
    suspend fun deleteWeatherResponse(modelRoot: ModelRoot)

    @Transaction
    suspend fun insertWeatherDataAfterDelete(weatherData: ModelRoot) {
        val existingWeatherData = getWeatherResponseFromLocal()
        existingWeatherData?.let {
            deleteWeatherResponse(it)
        }
        insertWeatherResponse(weatherData)
    }


    // favourite location
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavLocation(favAddress: FavAddress)

    @Query("select * from FavouriteTable")
    suspend fun getAllFavouritePlaces(): List<FavAddress>

    @Delete
    suspend fun deleteFavPlace(favAddress: FavAddress)

    // Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alertEntity: AlertEntity): Long

    @Query("SELECT * From AlertTable where id = :id")
    suspend fun getAlertsById(id: Int): AlertEntity

    @Delete
    suspend fun deleteAlert(alertEntity: AlertEntity)

    @Query("select * from AlertTable")
    suspend fun getAllAlerts(): List<AlertEntity>


}