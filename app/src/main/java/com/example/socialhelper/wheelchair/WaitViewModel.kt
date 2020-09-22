package com.example.socialhelper.wheelchair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaitViewModel(application: Application): AndroidViewModel(application){
    val readWrite = NetworkClient()
    var triedToConnect = false
    var madeFirstConnection = false
    var state = ""

    val data: LiveData<WheelData>
    private val wheelRepository: WheelRepository

    init {
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelDao.selectData(1)
    }

    //connect to the server and check state
    suspend fun connectToServer(){
        withContext(Dispatchers.IO){
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
       withContext(Dispatchers.IO){
             if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("helpRequest")
                 state = readWrite.readLine()
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