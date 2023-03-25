package com.example.weatherforecastapp

import android.app.Application
import com.example.weatherforecastapp.data.source.local.MyRoomDataBase
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.utils.NetworkConnectivityObserver

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkConnectivityObserver.initNetworkConnectivity(applicationContext)
        MyRoomDataBase.initRoom(applicationContext)
        MySharedPreferences.initSharedPref(applicationContext)
    }

}