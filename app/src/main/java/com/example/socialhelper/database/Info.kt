package com.example.socialhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class Info(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "password") val password: String,
    @ColumnInfo (name = "group") val group: String,
    @ColumnInfo(name = "key") var key: Int = 0
)