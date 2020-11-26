package com.example.socialhelper.verification

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.network.NetworkClient
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

    private val categoryEngList: Array<String> =
        application.resources.getStringArray(R.array.categoryEng)
    private val categoryList: Array<String> =
        application.resources.getStringArray(R.array.category)

    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    val readWrite = NetworkClient()


    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("verify")
                if (serverKey != 0) {
                    allInfo.value?.let {

                        var s = ""
                        when(it.category){
                            categoryList[0] -> s = categoryEngList[0]
                            categoryList[1] -> s = categoryEngList[1]
                            categoryList[2] -> s = categoryEngList[2]
                        }

                        readWrite.verify(serverKey,
                                        it.reference,
                                        it.name,
                                        it.surname,
                                        s)
                        Log.e("data", "sent")
                        serverId = readWrite.read()
                    }
                }
            }
        }
    }

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