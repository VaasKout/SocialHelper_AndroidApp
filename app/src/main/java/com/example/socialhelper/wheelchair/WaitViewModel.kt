package com.example.socialhelper.wheelchair

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class WaitViewModel(application: Application): AndroidViewModel(application){

    val readWrite = NetworkClient()
    var triedToConnect = false
    var madeFirstConnection = false
    var state = ""

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    val data: LiveData<WheelData>

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        data = repository.getWheelData(1)
    }

    //connect to the server and check state
    suspend fun connectToServer(){
        withContext(Dispatchers.IO){
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
        val job = uiScope.launch(Dispatchers.IO) {
            withTimeout(4000){
                    allInfo.value?.let {
                        if (readWrite.socket != null && readWrite.socket.isConnected) {
                            readWrite.writeLine("helpWaitWheel")
                            readWrite.writeLine(it.login)
                            state = readWrite.readLine()
                            Log.e("state", state)
                        }
                    }
            }
        }
        job.join()
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
            repository.deleteAllWheel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}