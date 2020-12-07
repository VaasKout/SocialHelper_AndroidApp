package com.example.socialhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Second table based on Room database
 *
 * it is used to store data of WheelChair client
 * when it makes an order and SocialWorker when
 * it takes an order
 *
 * WheelDao is Data Access Object with methods to Update, Delete and Insert the table
 * WheelDatabase is class to initialize database itself
 */

@Entity(tableName = "wheel_data")
data class WheelData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "first") val first: String,
    @ColumnInfo(name = "second") val second: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "comment") val comment: String = "",
    @ColumnInfo(name = "checked") var checked: Boolean = false,
    @ColumnInfo(name = "ordered") var ordered: Boolean = false
)