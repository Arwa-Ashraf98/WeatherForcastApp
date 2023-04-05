package com.example.weatherforecastapp.data.source.remote

import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {

    // @query data is not hidden and it send as key value pair
    // you put here relative link - the function that run on the server and get the data
    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = Constants.EXCLUDE_MINUTELY,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("lang") language: String = MySharedPreferences.getLanguage() as String,
    ): Response<ModelRoot>
}