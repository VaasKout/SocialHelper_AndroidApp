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

//    var bluetoothAnswer = 0
    val bluetoothReadWrite = BluetoothClient()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }


    private val _spotIsFree = MutableLiveData<Boolean>()
    val spotIsFree: LiveData<Boolean> = _spotIsFree

    fun onSetSpotFree() {
        _spotIsFree.value = true
    }

    fun onDoneSetSpotFree() {
        _spotIsFree.value = false
    }

    fun turnOnBluetooth(activity: Activity) {
        if (bluetoothReadWrite.btAdapter == null) {
            bluetoothReadWrite.findAdapter()
        }
        if (!bluetoothReadWrite.btAdapter.isEnabled && bluetoothReadWrite.btAdapter != null) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, BluetoothClient.REQUEST_ENABLE_BT)
        }
    }

    suspend fun createConnection() {
        withContext(Dispatchers.IO) {
            bluetoothReadWrite.createConnection()
        }
    }

     suspend fun sendMessage(s: String) {
        withContext(Dispatchers.IO) {
            bluetoothReadWrite.sendData(s)
        }
    }

//    private suspend fun receiveMessage() {
//        withContext(Dispatchers.IO) {
//            bluetoothAnswer = bluetoothReadWrite.receiveData()
//        }
//    }

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        bluetoothReadWrite.closeConnection()
        bluetoothReadWrite.btSocket = null
        Log.e("closed", "Port closed")
    }
}