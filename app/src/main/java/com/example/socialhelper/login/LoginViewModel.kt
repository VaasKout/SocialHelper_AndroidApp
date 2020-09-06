package com.example.socialhelper.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    var allInfo: LiveData<Info>
    val readWrite = AndroidClient()

    var login = ""
    var password = 0

    var serverID = 0
    var serverKey = 0

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        allInfo.value?.let {
        }
    }

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket("192.168.0.110", 9000)
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("loginPassword")
                if (login.isNotEmpty() && password > 0) {
                    readWrite.writeLoginPassword(login, password)

                    val name = readWrite.readLine()
                    val surname = readWrite.readLine()
                    val group = readWrite.readLine()
                    serverID = readWrite.read()
                    serverKey = readWrite.read()

                    if (serverID > 0 && serverKey > 0) {
                        val info = Info(
                            id = 1,
                            name = name,
                            surname = surname,
                            login = login,
                            password = password.toString(),
                            group = group,
                            serverID = serverID,
                            serverKey = serverKey
                        )

                        insertInfo(info)
                    }
                }
            }
        }
    }

    fun onClear() {
        uiScope.launch {
            deleteInfo()
        }
    }

    fun onUpdate(info: Info) {
        uiScope.launch {
            updateInfo(info)
        }
    }

    private suspend fun deleteInfo() {
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }

    private suspend fun insertInfo(info: Info) {
        withContext(Dispatchers.IO) {
            repository.insert(info)
        }
    }

    private suspend fun updateInfo(info: Info) {
        withContext(Dispatchers.IO) {
            repository.updateInfo(info)
        }
    }

    private val _navigateToMainFrag = MutableLiveData<Boolean>()
    val navigateToMainFrag: LiveData<Boolean> = _navigateToMainFrag

    private val _navigateToSignInFrag = MutableLiveData<Boolean>()
    val navigateToSignInFrag: LiveData<Boolean> = _navigateToSignInFrag

    fun onStartNavigationToMain() {
        _navigateToMainFrag.value = true
    }

    fun onDoneNavigationToMain() {
        _navigateToMainFrag.value = false
    }

    fun onStartNavigationToSign() {
        _navigateToSignInFrag.value = true
    }

    fun onDoneNavigationToSign() {
        _navigateToSignInFrag.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}