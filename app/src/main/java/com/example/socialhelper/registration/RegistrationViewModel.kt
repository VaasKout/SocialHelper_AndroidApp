package com.example.socialhelper.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.network.NetworkClient
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

    val readWrite = NetworkClient()
    var state: String = ""
    var referenceNumber = 0

    private val categoryEngList: Array<String> =
        application.resources.getStringArray(R.array.categoryEng)
    val categoryList: Array<String> = application.resources.getStringArray(R.array.category)

    //Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait

    //onClick methods

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
    //Connect and write data to the server
    suspend fun connectToServer() {
        withContext(Dispatchers.IO) {
            readWrite.connectSocket()
        }
    }


    suspend fun requestServer() {
        withContext(Dispatchers.IO) {
            allInfo.value?.let {
                var s = ""
                when(it.category){
                    categoryList[0] -> s = categoryEngList[0]
                    categoryList[1] -> s = categoryEngList[1]
                    categoryList[2] -> s = categoryEngList[2]
                }
                if (readWrite.socket != null && readWrite.socket.isConnected) {
                        readWrite.writeLine("regPregnant")
                        readWrite.writeUserRegData(
                            it.reference,
                            it.password.toInt(),
                            it.name,
                            it.surname,
                            it.login,
                            it.email,
                            s)
                    }
                    state = readWrite.readLine()
                }
            }
        }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}