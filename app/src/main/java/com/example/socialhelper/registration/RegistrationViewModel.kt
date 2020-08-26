package com.example.socialhelper.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel: ViewModel() {
    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> = _navigateToMain

    fun onStartNavigating(){
        _navigateToMain.value = true
    }
    fun onDoneNavigating(){
        _navigateToMain.value = false
    }
}