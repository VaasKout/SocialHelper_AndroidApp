package com.example.socialhelper.wheelchair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class WheelChairViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()
    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val data = MutableList(4){""}

//    var first = ""
//    var second = ""
//    var time = ""
//    var comment = ""

    private val _send = MutableLiveData<Boolean>()
    val send: LiveData<Boolean> = _send

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
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
            allInfo.value?.let {
                if (readWrite.socket != null
                    && readWrite.socket.isConnected ){
                    readWrite.writeLine("helpRequest")
                    readWrite.writeWheelchairData(
                        it.login, it.name, it.surname,
                        data[0], data[1], data[2],
                        data[3])
                }
            }
        }
    }

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