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
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.*

class WheelChairViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _send = MutableLiveData<Boolean>()
    val send: LiveData<Boolean> = _send
    var timeField = ""

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelRepository.selectData(1)
    }

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
                        data.first, data.second, data.time, data.comment)
                    }
                }
            }
        }
    }

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

//    fun onUpdate(wheelData: WheelData){
//        uiScope.launch {
//            updateData(wheelData)
//        }
//    }
//
//    private suspend fun updateData(wheelData: WheelData){
//        withContext(Dispatchers.IO){
//            wheelRepository.updateData(wheelData)
//        }
//    }

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