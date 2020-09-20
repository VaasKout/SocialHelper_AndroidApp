package com.example.socialhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "wheel_data")
data class WheelData(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "first") val first: String,
    @ColumnInfo (name = "second") val second: String,
    @ColumnInfo (name = "time") val time: String,
    @ColumnInfo (name = "comment") val comment: String = "",
    @ColumnInfo (name = "checked") var checked: Boolean = false,
    @ColumnInfo (name = "ordered") var ordered: Boolean = false
)