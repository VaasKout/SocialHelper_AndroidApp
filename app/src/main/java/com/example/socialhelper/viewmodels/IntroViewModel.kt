package com.example.socialhelper.viewmodels

import android.app.Application
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.DataBase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.repository.InfoRepository
import kotlinx.coroutines.*

class IntroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    val data: LiveData<List<WheelData>>

    //init data from databases

    init {
        val infoDao = DataBase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        data = repository.allWheelData
    }

    suspend fun animate(list: List<TextView>) {
        val appear =
            AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in)
        for (i in list) {
            i.alpha = 1.0F
            i.startAnimation(appear)
            delay(appear.duration)
            i.clearAnimation()
        }
        delay(appear.duration)
    }
}