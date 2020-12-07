package com.example.socialhelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.repository.InfoRepository

class ResponseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val userInfo: LiveData<Info>

    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        userInfo = repository.allInfo
    }

    //Live Data
    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private val _enter = MutableLiveData<Boolean>()
    val enter: LiveData<Boolean> = _enter

    //onClick methods
    fun onStartBackNavigation() {
        _navigateBack.value = true
    }

    fun onDoneBackNavigation() {
        _navigateBack.value = false
    }

    fun onStartEnterNavigation() {
        _enter.value = true
    }

    fun onDoneEnterNavigation() {
        _enter.value = false
    }
}