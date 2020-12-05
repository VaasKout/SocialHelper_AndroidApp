package com.example.socialhelper.wheelchair

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class WheelChairViewModel(application: Application) : AndroidViewModel(application) {
    val readWrite = NetworkClient()

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>

    var timeField = ""

    //initialize LiveData<Info> and LifeData<WheelData>
    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        data = repository.getWheelData(1)
    }


    //WheelDao methods
    fun onInsert(wheelData: WheelData) {
        viewModelScope.launch {
            insertData(wheelData)
        }
    }

    private suspend fun insertData(wheelData: WheelData) {
        withContext(Dispatchers.IO) {
            repository.insertWheel(wheelData)
        }
    }

    fun onUpdate(wheelData: WheelData) {
        viewModelScope.launch(Dispatchers.IO) {
            update(wheelData)
        }
    }

    private suspend fun update(wheelData: WheelData) {
        withContext(Dispatchers.IO) {
            repository.updateWheel(wheelData)
        }
    }

    //InfoDao methods
    fun onClear() {
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }

    //Connect and send data to the server
    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            allInfo.value?.let { info ->
                data.value?.let { data ->
                    Log.e("data", "is not null - id ${data.id}")
                    if (readWrite.socket != null
                        && readWrite.socket.isConnected
                    ) {
                        readWrite.writeLine("helpRequest")
                        readWrite.writeWheelchairData(
                            info.login, info.name, info.surname,
                            data.first, data.second, data.time,
                            data.comment
                        )
                        Log.e("sent", "helprequest")
                    }
                }
            }
        }
    }

    //onClick methods and LiveData
    private val _send = MutableLiveData<Boolean>()
    val send: LiveData<Boolean> = _send

    fun onStartSending() {
        _send.value = true
    }

    fun onDoneSending() {
        _send.value = false
    }
}