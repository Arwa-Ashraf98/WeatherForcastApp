package com.example.weatherforecastapp.data.source.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.weatherforecastapp.utils.Constants
import java.util.Locale

class MySharedPreferences private constructor() {
    companion object {
        private lateinit var appContext: Context

        fun initSharedPref(context: Context) {
            appContext = context
        }

        private fun getSharedPreferences(): SharedPreferences {
            return appContext.getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
        }


        fun setLatitude(latitude: Double?) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(Constants.LATITUDE, latitude.toString()).apply()
        }

        fun getLatitude(): Double? {
            val latitude = getSharedPreferences()
                .getString(Constants.LATITUDE, Constants.LAT_DEFAULT_VALUE.toString())
            return latitude?.toDouble()
        }


        fun setLongitude(longitude: Double?) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(Constants.LONGITUDE, longitude.toString()).apply()
        }

        fun getLongitude(): Double? {
            val longitude = getSharedPreferences()
                .getString(Constants.LONGITUDE, Constants.LONG_DEFAULT_VALUE.toString())

            return longitude?.toDouble()
        }

        fun setLocation(location: String?) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(Constants.LOCATION_CHOICE, location).apply()
        }

        fun getLocation(): String? {
            return getSharedPreferences()
                .getString(Constants.LOCATION_CHOICE, Constants.GPS_LOCATION_VALUES)
        }

        fun setTemperature(temp: String?) {
            val editor = getSharedPreferences().edit()
            editor.putString(Constants.TEMPERATURE_CHOICE, temp).apply()
        }

        fun getTemperature(): String? {
            return getSharedPreferences()
                .getString(Constants.TEMPERATURE_CHOICE, Constants.TEMP_KELVIN_VALUES)
        }

        fun setLanguage(language: String?) {
            val editor = getSharedPreferences().edit()
            editor.putString(Constants.LANGUAGE_CHOICE, language).apply()
        }

        fun getLanguage(): String? {
            Log.i("Shared", Locale.getDefault().language.toString())
            return getSharedPreferences()
                .getString(Constants.LANGUAGE_CHOICE, Locale.getDefault().language)
        }

        fun setWindSpeed(wind: String?) {
            val editor = getSharedPreferences().edit()
            editor.putString(Constants.WIND_CHOICE, wind).apply()
        }

        fun getWindSpeed(): String? {
            return getSharedPreferences()
                .getString(Constants.WIND_CHOICE, Constants.WIND_SPEED_METER_SEC_VALUES)
        }


        fun setAlarm(alarm: String?) {
            val editor = getSharedPreferences().edit()
            editor.putString(Constants.ALARM, alarm).apply()
        }

        fun getAlarm(): String? {
            return getSharedPreferences().getString(Constants.ALARM, Constants.NOTIFICATION)
        }


    }


}