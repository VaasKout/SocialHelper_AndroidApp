package com.example.socialhelper.repository

import androidx.lifecycle.LiveData
import com.example.socialhelper.database.WheelDao
import com.example.socialhelper.database.WheelData

//Repository for WheelDao
class WheelRepository(private val wheelDao: WheelDao){
    val allWheelData = wheelDao.getAllData()

    fun getData(key: Int): LiveData<WheelData>{
        return wheelDao.selectData(key)
    }

    suspend fun insert(wheelData: WheelData){
        wheelDao.insert(wheelData)
    }

    suspend fun updateData(wheelData: WheelData){
        wheelDao.updateData(wheelData)
    }

    suspend fun deleteAll(){
        wheelDao.deleteAll()
    }
}