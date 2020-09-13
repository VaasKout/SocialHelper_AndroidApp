package com.example.socialhelper.socialworker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SocialViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    private val _data = MutableLiveData<MutableList<WheelData>>()
    val data: LiveData<MutableList<WheelData>> = _data

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

//    suspend fun connectToServer() {
//        withContext(Dispatchers.IO) {
//            readWrite.connectSocket()
//        }
//    }
//
//    suspend fun requestServer() {
//        withContext(Dispatchers.IO) {
//            allInfo.value?.let {
//                if (readWrite.socket != null && readWrite.socket.isConnected) {
//                    /**
//                     * make categoties
//                     */
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


    //write "helpAccept"
    //refresh
    //