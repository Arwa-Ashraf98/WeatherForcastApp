package com.example.weatherforecastapp.data.source.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherforecastapp.utils.Constants

class MySharedPreferences private constructor() {
    companion object {
        private lateinit var appContext: Context

        fun initSharedPref(context: Context) {
            appContext = context
        }

        fun getSharedPreferences(): SharedPreferences {
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
                .getString(Constants.LATITUDE, Constants.LAT_VALUE.toString())

            return latitude?.toDouble()
        }


        fun setLongitude(longitude: Double?) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(Constants.LONGITUDE, longitude.toString()).apply()
        }

        fun getLongitude(): Double? {
            val longitude = getSharedPreferences()
                .getString(Constants.LONGITUDE, Constants.LON_VALUE.toString())

            return longitude?.toDouble()
        }

        fun setLocation(location: String?) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(Constants.LOCATION_CHOICE, location).apply()
        }

        fun getLocation(): String? {
            return getSharedPreferences()
                .getString(Constants.LOCATION_CHOICE, "")
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
            return getSharedPreferences()
                .getString(Constants.LANGUAGE_CHOICE, Constants.APP_LOCAL_EN_VALUES)
        }

        fun setWindSpeed(wind: String?) {
            val editor = getSharedPreferences().edit()
            editor.putString(Constants.WIND_CHOICE, wind).apply()
        }

        fun getWindSpeed(): String? {
            return getSharedPreferences()
                .getString(Constants.WIND_CHOICE,Constants.WIND_SPEED_METER_SEC_VALUES)
        }


    }


}