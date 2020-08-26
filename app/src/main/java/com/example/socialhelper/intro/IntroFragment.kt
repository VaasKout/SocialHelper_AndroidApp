package com.example.socialhelper.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentIntroBinding
import kotlinx.coroutines.*

class IntroFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentIntroBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)

        GlobalScope.launch {
            delay(5000)
            this@IntroFragment.findNavController()
                .navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
        }

        return binding.root
    }

}