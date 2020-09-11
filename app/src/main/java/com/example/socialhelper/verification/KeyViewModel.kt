package com.example.socialhelper.verification

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

class KeyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var serverKey = 0
    var serverId = 0
    var notificationShowed = false

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    val readWrite = AndroidClient()


    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket("192.168.0.110", 9000)
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("verify")
                if (serverKey != 0) {
                    allInfo.value?.let {
                        readWrite.verify(serverKey, it.reference,
                                        it.name, it.surname)
                        Log.e("data", "sent")
                        serverId = readWrite.read()
                    }
                }
            }
        }
    }

//    fun onUpdate(info: Info) {
//        uiScope.launch {
//            updateInfo(info)
//        }
//    }

    suspend fun updateInfo(info: Info) {
        withContext(Dispatchers.IO) {
            repository.updateInfo(info)
        }
    }

    fun onClear() {
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo() {
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private val _sendKey = MutableLiveData<Boolean>()
    val sendKey: LiveData<Boolean> = _sendKey

    private val _showNotification = MutableLiveData<Boolean>()
    val showNotification: LiveData<Boolean> = _showNotification

    fun onStartBackNavigation() {
        _navigateBack.value = true
    }

    fun onDoneNavigateBack() {
        _navigateBack.value = false
    }

    fun onSendKey() {
        _sendKey.value = true
    }

    fun onDoneSendKey() {
        _sendKey.value = false
    }

    fun onShow() {
        _showNotification.value = true
    }

    fun onDoneShow() {
        _showNotification.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}