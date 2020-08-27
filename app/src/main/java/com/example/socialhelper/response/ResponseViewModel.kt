package com.example.socialhelper.response

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ResponseViewModel (application: Application): AndroidViewModel(application){
    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.userInfo
    }

    //coroutine
//    private val viewModelJob = Job()
//    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }

}