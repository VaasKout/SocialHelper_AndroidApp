package com.example.socialhelper.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentIntroBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentIntroBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)

        val viewModel = ViewModelProvider(this)
            .get(IntroViewModel::class.java)

        val appear =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

        val wordList =
            listOf(binding.welcome1, binding.welcome2, binding.welcome3, binding.welcome4)

        val categoryList = resources.getStringArray(R.array.category)
        val categoryEngList = resources.getStringArray(R.array.categoryEng)


        lifecycleScope.launch {

            for (i in wordList) {
                i.alpha = 1.0F
                i.startAnimation(appear)
                delay(appear.duration)
                i.clearAnimation()
            }

            delay(appear.duration)

            this@IntroFragment
                .findNavController()
                .navigate(
                    IntroFragmentDirections
                        .actionIntroFragmentToKeyVerification()
                )

//            viewModel.allInfo.observe(viewLifecycleOwner, {
//
//                if (it == null || !it.wasLoggedIn){
//                    this@IntroFragment.findNavController()
//                        .navigate(IntroFragmentDirections.
//                        actionIntroFragmentToLoginFragment())
//                }
//                if (it != null && it.needVerification){
//                    this@IntroFragment
//                        .findNavController()
//                        .navigate(IntroFragmentDirections
//                            .actionIntroFragmentToKeyVerification())
//                }
//                if (it != null && it.wasLoggedIn){
//                    when(it.group){
//                        categoryList[0], categoryEngList[0] -> {
//                            this@IntroFragment.findNavController()
//                                .navigate(IntroFragmentDirections
//                                    .actionIntroFragmentToDisabledFragment())
//                        }
//                        categoryList[1], categoryEngList[1] -> {
//                            this@IntroFragment.findNavController()
//                                .navigate(IntroFragmentDirections
//                                    .actionIntroFragmentToPregnantFragment())
//                        }
//                        categoryList[2], categoryEngList[2] -> {
//                            this@IntroFragment.findNavController()
//                                .navigate(IntroFragmentDirections
//                                    .actionIntroFragmentToSocialFragment())
//                        }
//                    }
//                }
//            })

        }

        //            delay(appear.duration)
//            this@IntroFragment.findNavController()
//                .navigate(IntroFragmentDirections.actionIntroFragmentToDisabledMaps())

        binding.lifecycleOwner = this
        return binding.root
    }

}