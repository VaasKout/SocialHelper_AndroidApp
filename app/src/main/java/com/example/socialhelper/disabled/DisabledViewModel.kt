package com.example.socialhelper.disabled

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DisabledViewModel(application: Application): AndroidViewModel(application){


    private val _exit = MutableLiveData<Boolean>()
    val exit: LiveData<Boolean> = _exit

    fun startExit(){
        _exit.value = true
    }

    fun onDoneExit(){
        _exit.value = false
    }
}