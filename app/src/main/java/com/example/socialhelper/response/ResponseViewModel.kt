package com.example.socialhelper.response

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class ResponseViewModel (application: Application): AndroidViewModel(application){


    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//
        var serverKey = 0
//    val readWrite = AndroidClient()

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
        userInfo.value?.let {
            serverKey = it.serverKey
        }
    }

    fun onClear(){
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo(){
        withContext(Dispatchers.IO){
            repository.deleteInfo()
        }
    }

//
//    fun onUpdate(info: Info){
//        uiScope.launch {
//            updateInfo(info)
//        }
//    }
//
//    private suspend fun updateInfo(info: Info){
//        withContext(Dispatchers.IO){
//            repository.updateInfo(info)
//        }
//    }
//    suspend fun connectToServer(){
//        withContext(Dispatchers.IO){
//        readWrite.connectSocket("192.168.0.110", 9000)
//        }
//    }
//
//     suspend fun sendID(){
//            withContext(Dispatchers.IO){
//                userInfo.value?.let {
//                    if (readWrite.socket.isConnected){
//                readWrite.writeLine("userId")
//                readWrite.write(it.serverID)
//                 }
//                }
//        }
//    }
//
//     suspend fun readServerKey(){
//        withContext(Dispatchers.IO){
//            serverKey = readWrite.read()
//            Log.e("serverKey", serverKey.toString())
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
}