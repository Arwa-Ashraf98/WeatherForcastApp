package com.example.weatherforecastapp.data.local

import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.source.local.LocalSourceInterface

class FakeLocalDataSource(
    private var modelRoot: ModelRoot? = null,
    private val favouriteList: MutableList<FavAddress> = mutableListOf(),
    private val alertList: MutableList<AlertEntity> = mutableListOf(),
) : LocalSourceInterface {
    override suspend fun saveWeatherResponseAfterDelete(modelRoot: ModelRoot) {
        this.modelRoot = modelRoot
    }

    override suspend fun getWeatherDataLocally(): ModelRoot {
        return modelRoot ?: throw NullPointerException("This model has no data")
    }

    override suspend fun insertFavLocation(favAddress: FavAddress) {
        favouriteList.add(favAddress)
    }

    override suspend fun getFavPlaces(): List<FavAddress> {
        return favouriteList
    }

    override suspend fun deleteFavPlace(favAddress: FavAddress) {
        favouriteList.remove(favAddress)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long {
        alertList.add(alertEntity)
        return alertList.lastIndex.toLong()
    }

    override suspend fun getAlertById(id: Int?): AlertEntity {
        return alertList[id ?: 0]
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        alertList.remove(alertEntity)
    }

    override suspend fun getAllAlerts(): List<AlertEntity> {
        return alertList
    }
}