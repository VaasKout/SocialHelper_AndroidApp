package com.example.socialhelper.registration

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
import java.io.IOException

class RegistrationViewModel(application: Application): AndroidViewModel(application) {

    private val repository: InfoRepository

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
    }


    var onServerRequest: Boolean = false
    private val readWrite = AndroidClient()
//Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait

    private val _regInfo = MutableLiveData<Info>()
    val regInfo: LiveData<Info> = _regInfo

    fun onDoneNavigating(){
        _navigateToWait.value = false
    }
    fun onStartNavigating(){
        _navigateToWait.value = true
    }







    //Coroutines
    private val viewModelScope = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelScope)

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

    fun onRequestServer() = uiScope.launch {
        onServerRequest = requestServer()
    }

    private suspend fun requestServer(): Boolean{
        var connection: Boolean
        withContext(Dispatchers.IO){
            connection = if (readWrite.isAlive("192.168.0.105", 9000)){
                readWrite.writeLine("userRegData")
                true
            }else{
                false
            }
        }
        return connection
    }

}