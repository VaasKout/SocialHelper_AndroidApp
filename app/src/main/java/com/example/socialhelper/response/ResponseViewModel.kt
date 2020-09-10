package com.example.socialhelper.response

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository

class ResponseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
    }

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    fun onStartBackNavigation() {
        _navigateBack.value = true
    }

    fun onDoneBackNavigation() {
        _navigateBack.value = false
    }
}