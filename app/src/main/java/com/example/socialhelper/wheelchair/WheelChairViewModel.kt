package com.example.socialhelper.wheelchair

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.*

class WheelChairViewModel(application: Application): AndroidViewModel(application){
    val readWrite = NetworkClient()

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>

    //Coroutines
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var timeField = ""

    //initialize LiveData<Info> and LifeData<WheelData>
    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelRepository.allWheelData
    }


    //WheelDao methods
    fun onInsert(wheelData: WheelData){
        uiScope.launch {
            insertData(wheelData)
        }
    }

    private suspend fun insertData(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.insert(wheelData)
        }
    }

    fun onUpdate(wheelData: WheelData){
        uiScope.launch(Dispatchers.IO) {
            update(wheelData)
        }
    }

    suspend fun update(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.updateData(wheelData)
        }
    }

    //InfoDao methods
    fun onClear(){
        uiScope.launch {
            clear()
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
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
        withContext(Dispatchers.IO){
            allInfo.value?.let {info ->
           data.value?.let { data ->
                Log.e("data", "is not null - id ${data.id}")
                if (readWrite.socket != null
                    && readWrite.socket.isConnected ){
                    readWrite.writeLine("helpRequest")
                    readWrite.writeWheelchairData(
                        info.login, info.name, info.surname,
                        data.first, data.second, data.time,
                        data.comment)
                    Log.e("sent", "helprequest")
                    }
                }
            }
        }
    }

    //onClick methods and LiveData
    private val _send = MutableLiveData<Boolean>()
    val send: LiveData<Boolean> = _send

    fun onStartSending(){
        _send.value = true
    }
    fun onDoneSending(){
        _send.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}