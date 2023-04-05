package com.example.weatherforecastapp.data.source.local

import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.ui.favourite.view.FavAdapter
import kotlinx.coroutines.flow.Flow

class LocalSource private constructor() : LocalSourceInterface {

    companion object {
        @Volatile
        private var localSourceInstance: LocalSource? = null

        fun getLocalSourceInstance() = localSourceInstance ?: synchronized(this) {
            val temp = LocalSource()
            localSourceInstance = temp
            temp
        }
    }

    override suspend fun saveWeatherResponseAfterDelete(modelRoot: ModelRoot) {
        return MyRoomDataBase.getRoomInstance().getDao().insertWeatherDataAfterDelete(modelRoot)
    }

    override suspend fun getWeatherDataLocally(): ModelRoot {
        return MyRoomDataBase.getRoomInstance().getDao().getWeatherResponseFromLocal() as ModelRoot
    }

    override suspend fun insertFavLocation(favAddress: FavAddress) {
        return MyRoomDataBase.getRoomInstance().getDao().insertFavLocation(favAddress)
    }

    override suspend fun getFavPlaces(): List<FavAddress> {
        return MyRoomDataBase.getRoomInstance().getDao().getAllFavouritePlaces()
    }

    override suspend fun deleteFavPlace(favAddress: FavAddress) {
        return MyRoomDataBase.getRoomInstance().getDao().deleteFavPlace(favAddress)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long {
        return MyRoomDataBase.getRoomInstance().getDao().insertAlert(alertEntity)
    }

    override suspend fun getAlertById(id: Int?): AlertEntity {
        return MyRoomDataBase.getRoomInstance().getDao().getAlertsById(id ?: 0)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        return MyRoomDataBase.getRoomInstance().getDao().deleteAlert(alertEntity)
    }

    override suspend fun getAllAlerts(): List<AlertEntity> {
        return MyRoomDataBase.getRoomInstance().getDao().getAllAlerts()
    }


}