package com.example.weatherforecastapp.data.source.local

import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.ui.favourite.view.FavAdapter
import kotlinx.coroutines.flow.Flow

interface LocalSourceInterface {

    suspend fun saveWeatherResponseAfterDelete(modelRoot: ModelRoot)

    suspend fun getWeatherDataLocally(): ModelRoot


    // favourite
    suspend fun insertFavLocation(favAddress: FavAddress)

    suspend fun getFavPlaces(): List<FavAddress>

    suspend fun deleteFavPlace(favAddress: FavAddress)

    // alerts
    suspend fun insertAlert(alertEntity: AlertEntity): Long
    suspend fun getAlertById(id: Int?): AlertEntity
    suspend fun deleteAlert(alertEntity: AlertEntity)
    suspend fun getAllAlerts(): List<AlertEntity>


}
