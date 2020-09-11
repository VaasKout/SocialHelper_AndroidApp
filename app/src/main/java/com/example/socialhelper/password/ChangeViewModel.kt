package com.example.socialhelper.password

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeViewModel(application: Application): AndroidViewModel(application){

    val readWrite = AndroidClient()
    var receiveNewPass = 0
    var sendNewPass = 0

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    suspend fun updateInfo(info: Info){
        withContext(Dispatchers.IO){
            repository.updateInfo(info)
        }
    }

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            allInfo.value?.let {
                if (readWrite.socket != null && readWrite.socket.isConnected) {
                    if (sendNewPass > 0){
                        readWrite.writeLine("passChange")
                        readWrite.changePassword(
                            it.serverID,
                            sendNewPass)
                        receiveNewPass = readWrite.read()
                    }

                }
            }
        }
    }

    private val _changed = MutableLiveData<Boolean>()
    val changed: LiveData<Boolean> = _changed

    fun onStartChange(){
        _changed.value = true
    }

    fun onDoneChange(){
        _changed.value = false
    }
}