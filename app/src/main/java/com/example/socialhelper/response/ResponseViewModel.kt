package com.example.socialhelper.response

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository

class ResponseViewModel (application: Application): AndroidViewModel(application){


//    private val viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
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