package com.example.socialhelper.registration

import android.app.Application
import android.util.Log
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
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    var onServerRequest: Boolean = false
    private val readWrite = AndroidClient()
//Live Data
    private val _navigateToWait = MutableLiveData<Boolean>()
    val navigateToWait: LiveData<Boolean> = _navigateToWait

    private val _key = MutableLiveData<Int>()
    val key: LiveData<Int> = _key


    fun onDoneNavigating(){
        _navigateToWait.value = false
    }
    fun onStartNavigating(){
        _navigateToWait.value = true
    }

    //Coroutines
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    fun onClear(){
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo(){
        withContext(Dispatchers.IO){
            repository.deleteInfo()
        }
    }
    //Server methods
    fun onRequestServer() = uiScope.launch {
        onServerRequest = requestServer()
    }

    private suspend fun requestServer(): Boolean{
        var connection: Boolean
        withContext(Dispatchers.IO){
            connection = readWrite.isAlive("192.168.0.105", 9000)
        }
        return connection
    }

    /**
     * Ёбаный блять работай нахой сука
     */
//   fun fuck(){
//        uiScope.launch {
//            withContext(Dispatchers.IO){
//                readWrite.writeLine("userRegData")
//                readWrite.writeUserData("blet", "fuck", "help")
//                _key.postValue(readWrite.read())
//                Log.e("fuck", key.value.toString())
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}