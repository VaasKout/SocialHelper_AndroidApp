package com.example.socialhelper.socialworker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class SocialViewModel(application: Application): AndroidViewModel(application){

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }

    private val _exit = MutableLiveData<Boolean>()
    val exit: LiveData<Boolean> = _exit

    fun startExit(){
        _exit.value = true
    }

    fun onDoneExit(){
        _exit.value = false
    }

    fun onClear(){
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo(){
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }
}