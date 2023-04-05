package com.example.weatherforecastapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.NumberFormat
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Converters {
    companion object {
        const val DATETIME_PATTERN = "dd MMM, hh:mm aa"
        const val TIME_PATTERN_HOUR = "hh aa"
        const val TIME_PATTERN = "hh:mm a"
        const val DAY_PATTERN = "dd MMM"
        const val DAY ="dd MMM - EEE"
        const val DAY_PATTERN_DAY = "MMM dd, yyyy - EEE"
        const val DAY_ONLY ="EEE"
        const val DATE_PATTERN_HYPHEN = "dd-MM-yyyy"
        const val DATE_PATTERN_SLASH = "dd/MM/yyyy"
        const val WHOLE_DATE2 = "MMM dd, yyyy - EEE hh:mm a"
        const val WHOLE_DATE = "dd-MM-yyyy-hh:mm a"

        @SuppressLint("SimpleDateFormat")
        fun convertTimestampToString(dt: Long, type: String): String? {
            val timeStamp = Date(TimeUnit.SECONDS.toMillis(dt))
            return SimpleDateFormat(type).format(timeStamp)
        }


        private fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        private fun fromKelvinToFahrenheit(temp: Double): Double {
            return 1.8 * (temp - 273) + 32
        }

        private fun meterPerSecondToMilePerHour(speed: Double): Double {
            return speed * 2.237
        }

        fun convertTemperature(temp: Double, context: Context): String {
            if (MySharedPreferences.getLanguage().equals(Constants.APP_LOCAL_EN_VALUES, true)) {
            return when (MySharedPreferences.getTemperature()) {
                Constants.TEMP_CELSIUS_VALUES -> {
                    "${kelvinToCelsius(temp).toInt()}${context.getString(R.string.celsiusUnit)}"
                }
                Constants.TEMP_FAHRENHEIT_VALUES -> {
                    "${fromKelvinToFahrenheit(temp).toInt()}${context.getString(R.string.fahrenheitUnit)}"
                }
                else -> {
                    "${temp.toInt()}${context.getString(R.string.kelvinUnit)}"
                }
            }

            } else {
                return when (MySharedPreferences.getTemperature()) {
                    Constants.TEMP_CELSIUS_VALUES -> {
                        "${
                            convertNumberToArabic(
                                kelvinToCelsius(temp).toInt().toString()
                            )
                        }${context.getString(R.string.celsiusUnit)} "
                    }

                    Constants.TEMP_FAHRENHEIT_VALUES -> {
                        "${
                            convertNumberToArabic(
                                fromKelvinToFahrenheit(temp).toInt().toString()
                            )
                        }${context.getString(R.string.fahrenheitUnit)}"
                    }
                    else -> {
                        "${
                            convertNumberToArabic(temp.toInt().toString())
                        }${context.getString(R.string.kelvinUnit)}"
                    }
                }
        }
    }


    private fun convertNumberToArabic(englishNumber: String): String {
        val arabicNumbers = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val builder = StringBuilder()
        for (i in englishNumber) {
            if (englishNumbers.contains(i)) {
                builder.append(arabicNumbers[englishNumbers.indexOf(i)])
            } else {
                builder.append(i) // point
            }
        }
        return builder.toString()
    }

        fun convertHumidityOrPressure(input: Number): String {
            return if (MySharedPreferences.getLanguage().equals(Constants.APP_LOCAL_EN_VALUES , true)){
                input.toString()
            } else{
                convertNumberToArabic(input.toString())
            }
        }

    fun convertWindFromMeterPerSecondToMileOerHour(
        windSpeed: Double,
        context: Context
    ): String {
        if (MySharedPreferences.getLanguage().equals(Constants.APP_LOCAL_EN_VALUES, true)) {
            return when (MySharedPreferences.getWindSpeed()) {
                Constants.WIND_SPEED_MILE_HOUR_VALUES -> {
                    "${meterPerSecondToMilePerHour(windSpeed).toInt()}${context.getString(R.string.mile_hour)}"
                }
                else -> {
                    "${windSpeed.toInt()}${context.getString(R.string.meter_sec)}"
                }
            }

        } else {
            return when (MySharedPreferences.getWindSpeed()) {
                Constants.WIND_SPEED_MILE_HOUR_VALUES -> {
                    "${
                        convertNumberToArabic(
                            meterPerSecondToMilePerHour(windSpeed).toInt().toString()
                        )
                    }${context.getString(R.string.mile_hour)}"
                }
                else -> {
                    "${
                        convertNumberToArabic(
                            windSpeed.toInt().toString()
                        )
                    }${context.getString(R.string.meter_sec)}"
                }
            }
        }
    }
}
}
