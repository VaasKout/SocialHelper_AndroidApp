package com.example.socialhelper.repository

import androidx.lifecycle.LiveData
import com.example.socialhelper.database.WheelDao
import com.example.socialhelper.database.WheelData

class WheelRepository(private val wheelDao: WheelDao){
    val allWheelData = wheelDao.getAllData()

    suspend fun insert(wheelData: WheelData){
        wheelDao.insert(wheelData)
    }

    fun selectData(key: Int): LiveData<WheelData>{
        return wheelDao.selectData(key)
    }

    suspend fun updateData(wheelData: WheelData){
        wheelDao.updateData(wheelData)
    }

    suspend fun deleteData(wheelData: WheelData){
        wheelDao.deleteData(wheelData)
    }

    suspend fun deleteAll(){
        wheelDao.deleteAll()
    }
}