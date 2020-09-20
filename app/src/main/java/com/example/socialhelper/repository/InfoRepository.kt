package com.example.socialhelper.repository

import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDao

class InfoRepository (private val infoDao: InfoDao){
    val allInfo = infoDao.getAllInfo()

    suspend fun insert(info: Info){
        infoDao.insert(info)
    }
    suspend fun deleteInfo(){
        infoDao.deleteInfo()
    }
    suspend fun updateInfo(info: Info){
        infoDao.update(info)
    }
}