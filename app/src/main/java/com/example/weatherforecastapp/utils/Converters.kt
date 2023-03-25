package com.example.weatherforecastapp.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Converters {
    companion object {
        const val DATETIME_PATTERN = "dd MMM, hh:mm aa"
        const val TIME_PATTERN_HOUR = "hh aa"
        const val TIME_PATTERN = "hh:mm aa"
        const val DAY_PATTERN = "dd MMM"
        const val DATE_PATTERN = "dd-MMM-yyy"
        const val DATE_PATTERN_SLASH = "dd/MM/yyyy"

        @SuppressLint("SimpleDateFormat")
        fun convertTimestampToString(dt: Long, type: String): String? {
            val timeStamp = Date(TimeUnit.SECONDS.toMillis(dt))
            return SimpleDateFormat(type).format(timeStamp)
        }

        @SuppressLint("SimpleDateFormat")
        fun convertStringToTimestamp(dt: String, type: String): Long {
            return SimpleDateFormat(type).parse(dt)?.time ?: 0
        }

        fun convertTimeStampIntoHours(dt: Long): String {
            val date = Date(dt * 1000L)
            val format = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("GMT+2")
            }
            return format.format(date)
        }

        fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        fun fromKelvinToFahrenheit(temp: Double): Double {
            return 1.8 * (temp - 273) + 32
        }

        fun meterPerSecondToMilePerHour(speed: Double): Double {
            return speed * 2.237
        }

        fun convertTemperature(temp: Double, context: Context): String {
//            if (MySharedPreferences.getLanguage().equals(Constants.ENGLISH)){
            return when (MySharedPreferences.getTemperature()) {
                Constants.TEMP_CELSIUS_VALUES -> {
                    "${kelvinToCelsius(temp).toInt()} ${context.getString(R.string.celsiusUnit)}"
                }
                Constants.TEMP_FAHRENHEIT_VALUES -> {
                    "${fromKelvinToFahrenheit(temp).toInt()} ${context.getString(R.string.fahrenheitUnit)}"
                }
                else -> {
                    "${temp.toInt()} ${context.getString(R.string.kelvinUnit)}"
                }
            }

            //}
        }
    }
}