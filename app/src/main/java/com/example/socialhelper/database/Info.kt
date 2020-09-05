package com.example.socialhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class Info(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "surname") val surname: String,
    @ColumnInfo (name = "login") val login: String,
    @ColumnInfo (name = "password") val password: String,
    @ColumnInfo (name = "group") val group: String,
    @ColumnInfo (name = "reference") val reference: Int = 0,
    @ColumnInfo(name = "serverID") var serverID: Int = -1,
    @ColumnInfo(name = "serverKey") var serverKey: Int = -1
)