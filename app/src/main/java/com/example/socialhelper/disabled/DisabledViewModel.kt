package com.example.socialhelper.disabled

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class DisabledViewModel(application: Application): AndroidViewModel(application){

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
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
        withContext(Dispatchers.IO){
            repository.deleteInfo()
        }
    }
}