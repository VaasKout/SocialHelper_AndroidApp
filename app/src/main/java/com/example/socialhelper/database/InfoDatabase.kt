package com.example.socialhelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @see com.example.socialhelper.database.Info
 */

@Database(entities = [Info::class], version = 1, exportSchema = false)
abstract class InfoDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao

    companion object {
        @Volatile
        private var INSTANCE: InfoDatabase? = null

        fun getInfoDatabase(context: Context): InfoDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        InfoDatabase::class.java,
                        "user_info",
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}