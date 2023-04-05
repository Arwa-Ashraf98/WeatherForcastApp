package com.example.weatherforecastapp.data.source.local

import androidx.room.TypeConverter
import com.example.weatherforecastapp.data.models.Alerts
import com.example.weatherforecastapp.data.models.Current
import com.example.weatherforecastapp.data.models.Daily
import com.example.weatherforecastapp.data.models.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyConverters {
    @TypeConverter
    fun fromCurrentToJson(current: Current): String {
        return Gson().toJson(current)
    }

    // take String and convert into Current onc again
    @TypeConverter
    fun fromJsonToCurrent(value: String): Current {
        return Gson().fromJson(value, Current::class.java)
    }

    @TypeConverter
    fun fromDailyListToJson(list: List<Daily>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToDailyList(value: String): List<Daily> {
        val list = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(value, list)
    }


    @TypeConverter
    fun fromHourlyListToJson(list: List<Hourly>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToHourlyList(value: String): List<Hourly> {
        val typeList = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(value, typeList)
    }

    @TypeConverter
    fun fromJsonAlertList(value: String): List<Alerts>? {
        return if (value.isEmpty()) {
            null
        } else {
            val listType = object : TypeToken<List<Alerts>>() {}.type
            Gson().fromJson(value, listType)
        }
    }

    @TypeConverter
    fun toJsonAlertList(list: List<Alerts>?): String {
        return list?.let { Gson().toJson(it) } ?: ""
    }

}