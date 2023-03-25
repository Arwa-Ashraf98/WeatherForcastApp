package com.example.weatherforecastapp.data.source.local

import com.example.weatherforecastapp.data.models.ModelRoot
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


}