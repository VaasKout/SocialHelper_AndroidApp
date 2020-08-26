package com.example.socialhelper.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentIntroBinding
import kotlinx.coroutines.*

class IntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentIntroBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)

        val appear = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val wordList =
            listOf(binding.welcome1, binding.welcome2, binding.welcome3, binding.welcome4)

        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        uiScope.launch {

            for (i in wordList) {
                if (i == binding.welcome4) {
                    i.startAnimation(appear)
                    i.setTextColor(
                        ContextCompat
                            .getColor(requireContext(), R.color.colorPrimary)
                    )
                } else {
                i.startAnimation(appear)
                i.setTextColor(
                    ContextCompat
                        .getColor(requireContext(), R.color.textColor)
                )
                delay(550)
                }
            }
        }
        GlobalScope.launch {
            delay(3000)
            this@IntroFragment.findNavController()
                .navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
        }

        return binding.root
    }

}