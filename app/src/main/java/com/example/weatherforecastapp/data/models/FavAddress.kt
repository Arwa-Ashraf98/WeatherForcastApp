package com.example.weatherforecastapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecastapp.utils.Constants
import java.io.Serializable


@Entity(tableName = Constants.FAV_TABLE_NAME)
data class FavAddress(
    val lat: Double,
    val lon: Double,
    val city: String,
    @PrimaryKey()
    val latLong : String
): Serializable
