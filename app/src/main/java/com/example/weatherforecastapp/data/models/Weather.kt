package com.example.weatherforecastapp.data.models

data class Weather(
    val description: String = "",
    var icon: String = "",
    val id: Int = -1,
    val main: String = "",
)