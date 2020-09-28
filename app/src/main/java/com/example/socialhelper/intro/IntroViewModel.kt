package com.example.socialhelper.intro

import android.app.Application
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.database.InfoDatabase
import com.example.socialhelper.database.WheelData
import com.example.socialhelper.database.WheelDatabase
import com.example.socialhelper.repository.InfoRepository
import com.example.socialhelper.repository.WheelRepository
import kotlinx.coroutines.*

class IntroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InfoRepository
    val allInfo: LiveData<Info>
    val data: LiveData<List<WheelData>>
    private val wheelRepository: WheelRepository

    //init data from databases
    init {
        val infoDao = InfoDatabase.getInfoDatabase(application).infoDao()
        repository = InfoRepository(infoDao)
        allInfo = repository.allInfo
        val wheelDao = WheelDatabase.getWheelDatabase(application).wheelDao()
        wheelRepository = WheelRepository(wheelDao)
        data = wheelRepository.allWheelData
    }

    suspend fun animate(list: List<TextView>){
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