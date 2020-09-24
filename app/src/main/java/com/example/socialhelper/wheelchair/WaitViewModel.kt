package com.example.socialhelper.wheelchair

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.*

class WaitViewModel(application: Application): AndroidViewModel(application){
    val readWrite = NetworkClient()
    var triedToConnect = false
    var madeFirstConnection = false
    var state = ""

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelRepository.allWheelData
    }

    //connect to the server and check state
    suspend fun connectToServer(){
        withContext(Dispatchers.IO){
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
       withContext(Dispatchers.IO){
           allInfo.value?.let {
               if (readWrite.socket != null && readWrite.socket.isConnected) {
                   readWrite.writeLine("helpWaitWheel")
                   readWrite.writeLine(it.login)
                   state = readWrite.readLine()
                   Log.e("state", state)
//                   this.cancel()
               }
           }
        }
    }
    //Cancel order
    suspend fun cancelOrder(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("cancel")
            }
        }
    }

    //Clear WheelData database if order is complete or canceled
    suspend fun clear(){
        withContext(Dispatchers.IO){
            wheelRepository.deleteAll()
        }
    }
}