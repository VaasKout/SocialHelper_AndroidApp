package com.example.socialhelper.registration

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*
import java.io.IOException

class RegistrationViewModel(application: Application): AndroidViewModel(application) {

    var keyId = 0
    var serverKey = 0
    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    var onServerRequest: Boolean = false
    val readWrite = AndroidClient()
//Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait

    private val _key = MutableLiveData<Int>()
    val key: LiveData<Int> = _key


    fun onDoneNavigating(){
        _navigateToWait.value = false
    }
    fun onStartNavigating(){
        _navigateToWait.value = true
    }

    //Coroutines
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Database methods
    fun onInsert(info: Info){
        uiScope.launch {
            insertInfo(info)
        }
    }

    private suspend fun insertInfo(info: Info){
        withContext(Dispatchers.IO){
            repository.insert(info)
        }
    }

    fun onClear(){
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo(){
        withContext(Dispatchers.IO){
            repository.deleteInfo()
        }
    }
    fun onUpdate(info: Info){
        uiScope.launch {
            updateInfo(info)
        }
    }

    private suspend fun updateInfo(info: Info){
        withContext(Dispatchers.IO){
            repository.updateInfo(info)
        }
    }

//    fun onServerKey(){
//        uiScope.launch {
//            getServerKey()
//        }
//    }
//
//    private suspend fun getServerKey(){
//        allInfo.value?.let {
//            withContext(Dispatchers.IO){
//                    readWrite.connectSockect("192.168.0.110", 9000)
//                    readWrite.writeLine("userId")
//                    readWrite.write(it.key)
//                    serverKey = readWrite.read()
//                    Log.e("serverKey", serverKey.toString())
//            }
//        }
//    }
    //Server methods
    fun onRequestServer() = uiScope.launch {
         requestServer()
    }

    private suspend fun requestServer(){

        withContext(Dispatchers.IO) {
            allInfo.value?.let {
                try {
                    readWrite.connectSockect("192.168.0.110", 9000)
                    readWrite.writeLine("userRegData")
                    var s = ""
                    when (allInfo.value?.group) {
                        "Инвалид" -> s = "wheelchair"
                        "Беременная" -> s = "pregnant"
                        "Соц.работник" -> s = "socialworker"
                    }
                    readWrite.writeUserData(s, it.name, it.password.toInt())
                    keyId = readWrite.read()
                    Log.e("key", keyId.toString())
                } catch (e: IOException){
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}