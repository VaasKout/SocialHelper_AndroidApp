package com.example.socialhelper.database

data class WheelData(val id: Int = 0,
                     val name: String,
                     val first: String,
                     val second: String,
                     val time: String,
                     val comment: String,
                     var checked: Boolean = false)