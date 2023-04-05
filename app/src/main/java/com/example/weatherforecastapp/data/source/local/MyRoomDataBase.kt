package com.example.weatherforecastapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot

@Database(entities = [ModelRoot::class,FavAddress::class,AlertEntity::class], exportSchema = false, version = 7)
@TypeConverters(MyConverters::class)
abstract class MyRoomDataBase : RoomDatabase() {
    abstract fun getDao(): WeatherDao
    companion object {
        @Volatile
        private var roomDataBase: RoomDatabase? = null

        fun initRoom(context: Context) = roomDataBase ?: synchronized(this) {
            val temp = Room
                .databaseBuilder(context, MyRoomDataBase::class.java, "Weather_DataBase")
                .fallbackToDestructiveMigration()
                .build()
            this.roomDataBase = temp
            temp
        }

        fun getRoomInstance(): MyRoomDataBase = roomDataBase as MyRoomDataBase

    }
}