package com.example.weatherforecastapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecastapp.utils.Constants

@Entity(tableName = Constants.TABLE_NAME)
data class ModelRoot(
    @TypeConverters(Current::class)
    val current: Current,
    @TypeConverters(Daily::class)
    val daily: List<Daily>,
    @TypeConverters(Hourly::class)
    val hourly: List<Hourly>,
    @TypeConverters(Alerts::class)
    val alerts: List<Alerts> = listOf(),
    var cityName: String? = "",
    @PrimaryKey val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
)