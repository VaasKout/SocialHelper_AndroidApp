package com.example.socialhelper.pregnant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialhelper.R
import com.example.socialhelper.network.ReadWrite
import kotlinx.coroutines.*
import java.io.IOException
import java.net.ConnectException
import java.net.Socket

class PregnantViewModel: ViewModel(){

    private val viewModelScope = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelScope)

    private val _spotIsFree = MutableLiveData<Boolean>()
    val spotIsFree: LiveData<Boolean> = _spotIsFree

    private val _sentValue = MutableLiveData<String>()
    val sentValue: LiveData<String> = _sentValue

    fun onSetSpotFree(){
        _spotIsFree.value = true
    }

    fun onDoneSetSpotFree(){
        _spotIsFree.value = false
    }

    fun onRequest() = uiScope.launch {
        try {
            _sentValue.value = request()
        }catch (e: IOException){
            _sentValue.value = "Fuck"
        }
    }

    private suspend fun request(): String{
        var result: String
        withContext(Dispatchers.IO){
                val readWrite = ReadWrite()
            result = if (readWrite.isAlive("192.168.0.105", 9000)){
                readWrite.writeLine("userRegData")
                readWrite.writeUserData("pregnant", "Gay Gaivich Gayev", "110")
                val id = readWrite.read()
                id.toString()
            } else "No Connection"
        }
        return result
    }
}