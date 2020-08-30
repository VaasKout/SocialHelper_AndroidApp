package com.example.socialhelper.response

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class ResponseViewModel (application: Application): AndroidViewModel(application){
    private val readWrite = AndroidClient()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

     var serverKey: Int = 0

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
    }

    fun onServerKey(){
        uiScope.launch {
            getServerKey()
        }
    }


    //get ServerKey
    private suspend fun getServerKey(){
        userInfo.value?.let {
            withContext(Dispatchers.IO){
              readWrite.connectSocket("192.168.0.13", 9000)
                readWrite.writeLine("userId")
                readWrite.write(it.key)
                serverKey = readWrite.read()
                Log.e("serverKey", serverKey.toString())
            }
        }
    }
}