package com.example.socialhelper.pregnant

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.example.socialhelper.bluetooth.BluetoothClient
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class PregnantViewModel(application: Application) : AndroidViewModel(application) {
    //make instance of BluetoothClient()
    val bluetoothReadWrite = BluetoothClient()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repository: InfoRepository
//    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
//        allInfo = repository.allInfo
    }

    //onClick LiveData and methods

    private val _spotIsFree = MutableLiveData<Boolean>()
    val spotIsFree: LiveData<Boolean> = _spotIsFree

    fun onSetSpotFree() {
        _spotIsFree.value = true
    }

    fun onDoneSetSpotFree() {
        _spotIsFree.value = false
    }

    //find bluetooth, request to turn on bluetooth
    fun turnOnBluetooth(activity: Activity) {
        if (bluetoothReadWrite.btAdapter == null) {
            bluetoothReadWrite.findAdapter()
        }
        if (!bluetoothReadWrite.btAdapter.isEnabled && bluetoothReadWrite.btAdapter != null) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, BluetoothClient.REQUEST_ENABLE_BT)
        }
    }
    //close connection to prevent socket exceptions, connect to remote device
    suspend fun createConnection() {
        withContext(Dispatchers.IO) {
            bluetoothReadWrite.closeConnection()
            bluetoothReadWrite.createConnection()
        }
    }

     suspend fun sendMessage(s: String) {
        withContext(Dispatchers.IO) {
            bluetoothReadWrite.sendData(s)
        }
    }

    //InfoDao methods
    fun onClear() {
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo() {
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }

    //Destroy all connection after fragment is closed
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        bluetoothReadWrite.closeConnection()
        bluetoothReadWrite.btSocket = null
        Log.e("closed", "Port closed")
    }
}