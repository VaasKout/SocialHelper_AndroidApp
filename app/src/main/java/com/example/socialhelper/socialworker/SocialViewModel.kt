package com.example.socialhelper.socialworker

import android.app.Application
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

class SocialViewModel(application: Application): AndroidViewModel(application){
    val readWrite = NetworkClient()

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    var triedToConnect = false
    var madeFirstConnection = false

    val allInfo: LiveData<Info>
    val allData: LiveData<List<WheelData>>

    //Coroutines
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //initialize LiveData<Info> and LiveData<List<WheelData>>
    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        allInfo = repository.allInfo
        allData = wheelRepository.allWheelData
    }

    //Connect to the server and read data from it
    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun readData(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
//                readWrite.writeLine("request")
                val name = readWrite.readLine()
                val first = readWrite.readLine()
                val second = readWrite.readLine()
                val time = readWrite.readLine()
                val comment = readWrite.readLine()
                val data = WheelData(
                    name = name,
                    first = first,
                    second = second,
                    time = time,
                    comment = comment
                )
                onInsert(data)
            }
        }
    }

    //WheelDataDao methods
    fun onInsert(wheelData: WheelData){
        uiScope.launch {
            insert(wheelData)
        }
    }

    suspend fun insert(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.insert(wheelData)
        }
    }

    suspend fun deleteData(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.deleteData(wheelData)
        }
    }

    suspend fun updateData(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.updateData(wheelData)
        }
    }

    //Check if SocialWorker took an order
    fun onProcess(): Boolean{
        allData.value?.let {allData ->
            return allData.any { it.checked }
        }
        return false
    }

    //Check order status from server
    suspend fun acceptOrder(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("accept")
            }
        }
    }

    suspend fun completeOrder(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("complete")
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}