package com.example.socialhelper.wheelchair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaitViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()
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

    suspend fun cancelOrder(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("cancel")
            }
        }
    }
    suspend fun clear(){
        withContext(Dispatchers.IO){
            wheelRepository.deleteAll()
        }
    }
}