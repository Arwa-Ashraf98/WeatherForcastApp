package com.example.weatherforecastapp.utils

import com.example.weatherforecastapp.R

class HandleIcon {
    companion object {
        fun getIcon(imageString: String): Int {
            val imageId: Int
            when (imageString) {
                "01d" -> imageId = R.drawable.icon_01d
                "01n" -> imageId = R.drawable.icon_01n
                "02d" -> imageId = R.drawable.icon_02d
                "02n" -> imageId = R.drawable.icon_02n
                "03n" -> imageId = R.drawable.icon_03n
                "03d" -> imageId = R.drawable.icon_03d
                "04d" -> imageId = R.drawable.icon_04d
                "04n" -> imageId = R.drawable.icon_04n
                "09d" -> imageId = R.drawable.icon_09d
                "09n" -> imageId = R.drawable.icon_09n
                "10d" -> imageId = R.drawable.icon_10d
                "10n" -> imageId = R.drawable.icon_10n
                "11d" -> imageId = R.drawable.icon_11d
                "11n" -> imageId = R.drawable.icon_11n
                "13d" -> imageId = R.drawable.icon_13d
                "13n" -> imageId = R.drawable.icon_13n
                "50d" -> imageId = R.drawable.icon_50d
                "50n" -> imageId = R.drawable.icon_50n
                else -> imageId = R.drawable.clouds
            }
            return imageId
        }
    }
}