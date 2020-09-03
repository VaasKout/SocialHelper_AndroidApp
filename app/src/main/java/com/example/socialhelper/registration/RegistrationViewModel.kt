package com.example.socialhelper.registration

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

class RegistrationViewModel(application: Application): AndroidViewModel(application) {

    var keyId = 0
    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    val readWrite = AndroidClient()
//Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait


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

    suspend fun connectToServer(){
        withContext(Dispatchers.IO) {
            readWrite.connectSocket("192.168.0.110", 9000)
        }
    }

    suspend fun requestServer(){
         withContext(Dispatchers.IO) {
            allInfo.value?.let {
                    var s = ""
                    when (it.group) {
                        "Инвалид" -> s = "wheelchair"
                        "Беременная" -> s = "pregnant"
                        "Соц.работник" -> s = "socialworker"
                    }
                    if ( readWrite.socket != null && readWrite.socket.isConnected){
                    readWrite.writeLine("userRegData")
                    readWrite.writeUserData(s, it.name, it.password.toInt())
                    keyId = readWrite.read()
                    Log.e("key", keyId.toString())
                    }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}