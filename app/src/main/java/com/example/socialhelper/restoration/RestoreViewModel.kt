package com.example.socialhelper.restoration

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

class RestoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    var allInfo: LiveData<Info>
    val readWrite = AndroidClient()
    var loginRestore = ""
    var postRestore = ""

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket("192.168.0.110", 9000)
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            if (readWrite.socket != null &&
                readWrite.socket.isConnected &&
                loginRestore.isNotEmpty() &&
                postRestore.isNotEmpty()
            ) {
                readWrite.writeLine("loginPost")
                readWrite.writeRestoreInfo(loginRestore, postRestore)
            }
        }
    }


    private val _restoreNavigateToLogin = MutableLiveData<Boolean>()
    val restoreNavigateToLogin: LiveData<Boolean> = _restoreNavigateToLogin

    private val _navigateBackToLogin = MutableLiveData<Boolean>()
    val navigateBackToLogin: LiveData<Boolean> = _navigateBackToLogin


    fun onStartRestore() {
        _restoreNavigateToLogin.value = true
    }

    fun onDoneRestore() {
        _restoreNavigateToLogin.value = false
    }

    fun onNavigateBack() {
        _navigateBackToLogin.value = true
    }

    fun onDoneNavigateBack() {
        _navigateBackToLogin.value = false
    }

}