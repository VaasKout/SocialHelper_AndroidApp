package com.example.socialhelper.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

        /**
         * make fade anim from 1 to 0 in 1 sec and back
         */

        val appear = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)


        val wordList =
            listOf(binding.welcome1, binding.welcome2, binding.welcome3, binding.welcome4)

       lifecycleScope.launch {
            for (i in wordList) {
                i.alpha = 1.0F
                i.startAnimation(appear)
                delay(800)
            }
//           delay(600)
//           this@IntroFragment
//               .findNavController()
//               .navigate(IntroFragmentDirections.actionIntroFragmentToPregnantFragment())
//        }

            delay(600)
            this@IntroFragment.findNavController()
                .navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
                }

        return binding.root
    }

}