package com.example.socialhelper.repository

import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDao

class InfoRepository (private val infoDao: InfoDao){
    val userInfo: LiveData<Info> = infoDao.getAllInfo()

    suspend fun insert(info: Info){
        infoDao.insert(info)
    }
    suspend fun deleteInfo(){
        infoDao.deleteInfo()
    }
}