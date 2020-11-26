package com.example.socialhelper.password

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.network.NetworkClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeViewModel(application: Application): AndroidViewModel(application){

    val readWrite = NetworkClient()
    var receiveNewPass = 0
    var sendNewPass = 0

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    suspend fun updateInfo(info: Info){
        withContext(Dispatchers.IO){
            repository.updateInfo(info)
        }
    }
    //Connect and send new password
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

    //LiveData
    private val _changed = MutableLiveData<Boolean>()
    val changed: LiveData<Boolean> = _changed

    //onClick methods

    fun onStartChange(){
        _changed.value = true
    }

    fun onDoneChange(){
        _changed.value = false
    }
}