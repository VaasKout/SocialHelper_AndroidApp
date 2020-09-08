package com.example.socialhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class Info(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo (name = "name") var name: String,
    @ColumnInfo (name = "surname") var surname: String,
    @ColumnInfo (name = "login") var login: String,
    @ColumnInfo (name = "password") var password: String,
    @ColumnInfo (name = "group") var group: String,
    @ColumnInfo (name = "post") var email: String,
    @ColumnInfo (name = "reference") val reference: Int = 0,
    @ColumnInfo (name = "serverID") var serverID: Int = -1,
    @ColumnInfo (name = "serverKey") var serverKey: Int = -1,
    @ColumnInfo (name = "wasLoggedIn") var wasLoggedIn: Boolean = false,
    @ColumnInfo (name = "wasVerified") var needVerification: Boolean = false
)