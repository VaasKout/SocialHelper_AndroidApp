package com.example.socialhelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WheelData::class], version = 1, exportSchema = false)
abstract class WheelDatabase: RoomDatabase(){
    abstract fun wheelDao(): WheelDao
    companion object {
        @Volatile
        private var INSTANCE: WheelDatabase? = null
        fun getWheelDatabase(context: Context): WheelDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WheelDatabase::class.java,
                        "wheel_data",
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}