package com.example.socialhelper.pregnant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.net.Socket

class PregnantViewModel: ViewModel(){
    private val viewModelScope = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelScope)

    private val _spotIsFree = MutableLiveData<Boolean>()
    val spotIsFree: LiveData<Boolean> = _spotIsFree

    fun onSetSpotFree(){
        _spotIsFree.value = true
    }

    fun onDoneSetSpotFree(){
        _spotIsFree.value = false
    }
}