package com.example.socialhelper.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DialogViewModel(application: Application): AndroidViewModel(application){

    private val _close = MutableLiveData<Boolean>()
    val close: LiveData<Boolean> = _close

    fun onStartClose(){
        _close.value = true
    }

    fun onDoneClose(){
        _close.value = false
    }
}