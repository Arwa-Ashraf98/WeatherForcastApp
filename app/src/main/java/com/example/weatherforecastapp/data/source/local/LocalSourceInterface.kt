package com.example.weatherforecastapp.data.source.local

import com.example.weatherforecastapp.data.models.ModelRoot
import kotlinx.coroutines.flow.Flow

interface LocalSourceInterface {

    suspend fun saveWeatherResponseAfterDelete(modelRoot: ModelRoot)

    suspend fun getWeatherDataLocally() : ModelRoot
}
