package com.example.socialhelper.wheelchair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.socialhelper.network.AndroidClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaitViewModel(application: Application): AndroidViewModel(application){
    val readWrite = AndroidClient()

    suspend fun requestServer() {
       withContext(Dispatchers.IO){
             if (readWrite.socket != null && readWrite.socket.isConnected) {
                readWrite.writeLine("helpRequest")
            }
        }
    }
}