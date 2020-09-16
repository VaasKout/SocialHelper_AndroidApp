package com.example.socialhelper.socialworker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.*

class SocialViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()

    private val repository: InfoRepository
    private val wheelRepository: WheelRepository
    private var selected: List<WheelData>? = null

    val allInfo: LiveData<Info>
    val allData: LiveData<List<WheelData>>

    private val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        allInfo = repository.allInfo
        allData = wheelRepository.allWheelData
    }

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun readData(){
        withContext(Dispatchers.IO){

        }
    }

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

    fun onDeleteAll(){
        uiScope.launch {
            deleteAll()
        }
    }

    private suspend fun deleteAll(){
        withContext(Dispatchers.IO){
            wheelRepository.deleteAll()
        }
    }


    fun onProcess(): Boolean{
        allData.value?.let {allData ->
            return allData.any { it.checked }
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


//    suspend fun requestServer() {
//        withContext(Dispatchers.IO) {
//            allInfo.value?.let {
//                if (readWrite.socket != null && readWrite.socket.isConnected) {
                    /**
                     * make categoties
                     */
//                    readWrite.writeLine("HelpGet")
//                    val login = readWrite.readLine()
//                    val name = readWrite.readLine()
//                    val surname = readWrite.readLine()
//                    val first = readWrite.readLine()
//                    val second = readWrite.readLine()
//                    val time = readWrite.readLine()
//                    val comment = readWrite.readLine()
//
//                    _data.value?.add(WheelData(name = name, first = first,
//                        second = second, time = time, comment = comment))
//                }
//            }
//        }
    }