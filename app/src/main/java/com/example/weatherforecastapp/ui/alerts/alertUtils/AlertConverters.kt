package com.example.weatherforecastapp.ui.alerts.alertUtils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.example.weatherforecastapp.utils.Converters
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AlertConverters {




    @SuppressLint("SimpleDateFormat")
    fun dateTimeConverterTimestampToString(dt: Long, context: Context): String? {
        val timeStamp = Date(TimeUnit.SECONDS.toMillis(dt))
        return SimpleDateFormat("dd MMM, hh:mm aa", getCurrentLocale(context)).format(timeStamp)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDayToString(dt: Long, context: Context): String? {
        val timeStamp = Date(TimeUnit.SECONDS.toMillis(dt))
        return SimpleDateFormat("dd MMM", getCurrentLocale(context)).format(timeStamp)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeToString(dt: Long, context: Context): String? {
        val timeStamp = Date(TimeUnit.SECONDS.toMillis(dt))
        return SimpleDateFormat("hh:mm aa", getCurrentLocale(context)).format(timeStamp)
    }

    fun formatDate(year: Int, month: Int, date: Int): String {
        val calender = Calendar.getInstance()
        calender.set(year, month, date)
        return SimpleDateFormat(
            Converters.DATE_PATTERN_HYPHEN,
            Locale.getDefault()
        ).format(calender.time)
    }

    fun formatTime(hour: Int, minute: Int): String {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)
        return SimpleDateFormat(Converters.TIME_PATTERN, Locale.getDefault()).format(calender.time)
    }


    private fun getCurrentLocale(context: Context): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    fun convertDateToLong(date: String,context: Context): Long {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", getCurrentLocale(context))
        val timestamp: Date = simpleDateFormat.parse(date) as Date
        return timestamp.time
    }
}