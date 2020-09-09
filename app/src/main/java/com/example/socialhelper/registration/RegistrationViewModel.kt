package com.example.socialhelper.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.AndroidClient
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }


    val readWrite = AndroidClient()
    var state: String = ""
    var referenceNumber = 0

    //Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait


    fun onDoneNavigating() {
        _navigateToWait.value = false
    }

    fun onStartNavigating() {
        _navigateToWait.value = true
    }

    //Coroutines
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Database methods
    fun onInsert(info: Info) {
        uiScope.launch {
            insertInfo(info)
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

    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket("192.168.0.110", 9000)
        }
    }

    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            allInfo.value?.let {
                var g = ""
                when (it.group) {
                    "Инвалид" -> g = "wheelchair"
                    "Беременная" -> g = "pregnant"
                    "Соц.работник" -> g = "socialworker"
                }

                if (readWrite.socket != null && readWrite.socket.isConnected) {
                    if (g == "wheelchair" || g == "socialworker") {
                        readWrite.writeLine("userRegData")
                        readWrite.writeUserData(
                            g,
                            it.name,
                            it.surname,
                            it.login,
                            it.password.toInt(),
                            it.email
                        )
                    } else if (g == "pregnant") {
                        readWrite.writeLine("regPregnant")
                        readWrite.writePregnantData(
                            it.reference,
                            it.name,
                            it.surname,
                            it.login,
                            it.password.toInt(),
                            it.email
                        )
                    }
                    state = readWrite.readLine()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}