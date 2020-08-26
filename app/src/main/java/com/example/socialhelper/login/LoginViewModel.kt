package com.example.socialhelper.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    private val _navigateToMainFrag = MutableLiveData<Boolean>()
    val navigateToMainFrag: LiveData<Boolean> = _navigateToMainFrag

    private val _navigateToSignInFrag = MutableLiveData<Boolean>()
    val navigateToSignInFrag: LiveData<Boolean> = _navigateToSignInFrag

    fun onStartNavigationToMain(){
        _navigateToMainFrag.value = true
    }
    fun onDoneNavigationToMain(){
        _navigateToMainFrag.value = false
    }
    fun onStartNavigationToSign(){
        _navigateToSignInFrag.value = true
    }
    fun onDoneNavigationToSign(){
        _navigateToSignInFrag.value = false
    }
}