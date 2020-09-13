package com.example.socialhelper.login

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
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("login")
                if (login.isNotEmpty() && password > 0) {
                    readWrite.writeLoginPassword(login, password)
                    serverID = readWrite.read()
                    password = readWrite.read()
                    serverKey = readWrite.read()
                    val name = readWrite.readLine()
                    val surname = readWrite.readLine()
                    login = readWrite.readLine()
                    val email = readWrite.readLine()
                    val category = readWrite.readLine()
                    Log.e("name", name)
                    Log.e("surname", surname)
                    Log.e("login", login)
                    Log.e("password", password.toString())
                    Log.e("serverID", serverID.toString())
                    Log.e("serverKey", serverKey.toString())

                    if (serverID > 0 && serverKey > 0) {
                        Log.e("info", "Info inserted")
                        val info = Info(
                            id = 1,
                            name = name,
                            surname = surname,
                            login = login,
                            password = password.toString(),
                            email = email,
                            serverID = serverID,
                            serverKey = serverKey,
                            category = category)
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

    suspend fun updateInfo(info: Info) {
        withContext(Dispatchers.IO) {
            repository.updateInfo(info)
        }
    }

    private val _navigateToMainFrag = MutableLiveData<Boolean>()
    val navigateToMainFrag: LiveData<Boolean> = _navigateToMainFrag

    private val _navigateToSignInFrag = MutableLiveData<Boolean>()
    val navigateToSignInFrag: LiveData<Boolean> = _navigateToSignInFrag

    private val _navigateToRestoreFrag = MutableLiveData<Boolean>()
    val navigateToRestoreFrag: LiveData<Boolean> = _navigateToRestoreFrag

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

    fun onStartNavigationToRestore() {
        _navigateToRestoreFrag.value = true
    }

    fun onDoneNavigationToRestore() {
        _navigateToRestoreFrag.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}