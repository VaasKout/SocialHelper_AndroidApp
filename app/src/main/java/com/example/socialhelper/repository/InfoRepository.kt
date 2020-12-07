package com.example.socialhelper.repository

import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDao
import com.example.socialhelper.database.WheelData

//Repository for InfoDao
class InfoRepository(private val infoDao: InfoDao) {
    val allInfo = infoDao.getAllInfo()

    suspend fun insertInfo(info: Info) {
        infoDao.insertInfo(info)
    }

    suspend fun deleteInfo() {
        infoDao.deleteInfo()
    }

    suspend fun updateInfo(info: Info) {
        infoDao.updateInfo(info)
    }

    val allWheelData = infoDao.getAllWheelData()

    fun getWheelData(key: Int): LiveData<WheelData> {
        return infoDao.selectWheelData(key)
    }

    suspend fun insertWheel(wheelData: WheelData) {
        infoDao.insertWheel(wheelData)
    }

    suspend fun updateWheel(wheelData: WheelData) {
        infoDao.updateWheel(wheelData)
    }

    suspend fun deleteAllWheel() {
        infoDao.deleteAllWheel()
    }
}