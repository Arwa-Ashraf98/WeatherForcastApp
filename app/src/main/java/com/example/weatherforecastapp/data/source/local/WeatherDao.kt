package com.example.weatherforecastapp.data.source.local

import androidx.room.*
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


}