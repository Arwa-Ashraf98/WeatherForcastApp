package com.example.weatherforecastapp.data.source.remote

import com.example.weatherforecastapp.data.models.ModelRoot

sealed class APIState{
    class OnSuccess(val weatherData: ModelRoot):APIState()
    class OnFail(val errorMessage: Throwable ):APIState()
    object Loading : APIState()
}