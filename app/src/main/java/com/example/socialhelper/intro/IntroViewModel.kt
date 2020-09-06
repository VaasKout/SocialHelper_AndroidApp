package com.example.socialhelper.intro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.repository.InfoRepository

class IntroViewModel(application: Application): AndroidViewModel(application){

    private val repository: InfoRepository
    val allInfo: LiveData<Info>

    init {
        val infoDao = InfoDatabase.getDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
    }
}