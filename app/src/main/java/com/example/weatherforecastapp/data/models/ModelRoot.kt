package com.example.weatherforecastapp.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecastapp.data.source.local.MyConverters
import com.example.weatherforecastapp.utils.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = Constants.TABLE_NAME)
data class ModelRoot(
    @PrimaryKey
    @NonNull
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: String,
    @TypeConverters
    val current: Current,
    @TypeConverters
    val hourly: List<Hourly>,
    @TypeConverters
    val daily: List<Daily>,
    @TypeConverters
    @SerializedName("alerts") var alerts: List<Alerts>?= null
)