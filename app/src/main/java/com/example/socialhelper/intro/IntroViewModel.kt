package com.example.socialhelper.intro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository

class IntroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>
    private val wheelRepository: WheelRepository

    //Flag is used to avoid bug when animation ends but navigation doesn't happen
    //after device rotation and onStop() lifecycle method
    var nav = false

    //init data from databases
    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelDao.selectData(1)
    }
}