package com.example.socialhelper.pregnant

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.lifecycle.*
import com.example.socialhelper.bluetooth.BluetoothClient
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class PregnantViewModel(application: Application): AndroidViewModel(application){

    var bluetoothAnswer = 0
    val bluetoothReadWrite = BluetoothClient()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }



    private val _spotIsFree = MutableLiveData<Boolean>()
    val spotIsFree: LiveData<Boolean> = _spotIsFree

    private val _exit = MutableLiveData<Boolean>()
    val exit: LiveData<Boolean> = _exit

    fun startExit(){
        _exit.value = true
    }

    fun onDoneExit(){
        _exit.value = false
    }

    fun onSetSpotFree(){
        _spotIsFree.value = true
    }

    fun onDoneSetSpotFree(){
        _spotIsFree.value = false
    }

      fun turnOnBluetooth(activity: Activity){
          if (bluetoothReadWrite.btAdapter == null){
              bluetoothReadWrite.findAdapter()
          }
         if (!bluetoothReadWrite.btAdapter.isEnabled && bluetoothReadWrite.btAdapter != null){
             val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
             activity.startActivityForResult(enableBtIntent, BluetoothClient.REQUEST_ENABLE_BT)
            }
     }

    suspend fun createConnection(){
        withContext(Dispatchers.IO){
            bluetoothReadWrite.createConnection()
        }
    }

    fun startBluetoothTransaction(i: Int){
       uiScope.launch {
            sendMessage(i)
            receiveMessage()
       }
    }

    private suspend fun sendMessage(i: Int){
        withContext(Dispatchers.IO){
            bluetoothReadWrite.sendData(i)
        }
    }
    private suspend fun receiveMessage(){
        withContext(Dispatchers.IO){
            bluetoothAnswer = bluetoothReadWrite.receiveData()
        }
    }

    fun onClear(){
        uiScope.launch {
            deleteInfo()
        }
    }

    private suspend fun deleteInfo(){
        withContext(Dispatchers.IO) {
            repository.deleteInfo()
        }
    }
}