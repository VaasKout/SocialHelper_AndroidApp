package com.example.socialhelper.response

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class ResponseViewModel (application: Application): AndroidViewModel(application){

    private val readWrite = AndroidClient()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

     var serverKey = 0

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
    }

    fun onUpdate(info: Info){
        uiScope.launch {
            updateInfo(info)
        }
    }

    private suspend fun updateInfo(info: Info){
        withContext(Dispatchers.IO){
            repository.updateInfo(info)
        }
    }

    fun onServerKey(){
            uiScope.launch{
                getServerKey()
                while (serverKey <= 0){
                    readKey()
                    delay(3000)
                }
            }
    }

    //get ServerKey
    private suspend fun getServerKey(){
            withContext(Dispatchers.IO){
                readWrite.connectSocket("192.168.0.13", 9000)
                delay(100)
                userInfo.value?.let {
                    if (readWrite.socket.isConnected){
                readWrite.writeLine("userId")
                readWrite.write(it.serverID)
                 }
                }
        }
    }

    private suspend fun readKey(){
        withContext(Dispatchers.IO){
            serverKey = readWrite.read()
            Log.e("serverKey", serverKey.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}