package com.example.socialhelper.socialworker

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

class SocialViewModel(application: Application): AndroidViewModel(application){
    val readWrite = NetworkClient()

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    var triedToConnect = false
    var madeFirstConnection = false

    val allInfo: LiveData<Info>
    val data: LiveData<List<WheelData>>

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
        data = wheelRepository.allWheelData
    }

    //Connect to the server and read data from it
    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun readData(){
//        var login = ""
        var firstName = ""
//        var lastName = ""
        var first = ""
        var second = ""
        var time = ""
        var comment = ""
       val job = uiScope.launch(Dispatchers.IO){
           withTimeout(4000){
               if (readWrite.socket != null && readWrite.socket.isConnected) {
                   readWrite.writeLine("helpGet")
//                   login = readWrite.readLine()
                   firstName = readWrite.readLine()
//                   lastName = readWrite.readLine()
                   first = readWrite.readLine()
                   second = readWrite.readLine()
                   time = readWrite.readLine()
                   comment = readWrite.readLine()
                   Log.e("name", firstName)
               }
           }
        }
        job.join()
        if (time.isNotEmpty()){
            val data = WheelData(
                id = 1,
                name = firstName,
                first = first,
                second = second,
                time = time,
                comment = comment
            )
            insert(data)
        }
    }

    //InfoDao method
    suspend fun clear(){
        withContext(Dispatchers.IO){
            repository.deleteInfo()
        }
    }

    //WheelDataDao methods

//    private fun onInsert(wheelData: WheelData){
//       uiScope.launch {
//            insert(wheelData)
//        }
//    }

    suspend fun insert(wheelData: WheelData){
       withContext(Dispatchers.IO){
            wheelRepository.insert(wheelData)
        }
    }

    fun deleteAll(){
        uiScope.launch {
            deleteData()
            clear()
        }
    }

    suspend fun deleteData(){
        withContext(Dispatchers.IO){
            wheelRepository.deleteAll()
        }
    }

    suspend fun updateData(wheelData: WheelData){
        withContext(Dispatchers.IO){
            wheelRepository.updateData(wheelData)
        }
    }

    //Check if SocialWorker took an order
    fun onProcess(): Boolean{
        data.value?.let {allData ->
            return allData.any { it.checked }
        }
        return false
    }

    //Check order status from server
    suspend fun acceptOrder(){
        withContext(Dispatchers.IO){
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("accepted")
                Log.e("accept", "yes")
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}