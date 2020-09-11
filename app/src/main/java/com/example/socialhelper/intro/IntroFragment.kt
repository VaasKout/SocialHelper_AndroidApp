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


        lifecycleScope.launch {

            for (i in wordList) {
                i.alpha = 1.0F
                i.startAnimation(appear)
                delay(appear.duration)
                i.clearAnimation()
            }

            delay(appear.duration)

            viewModel.allInfo.observe(viewLifecycleOwner, {

                if (it == null || (!it.wasLoggedIn && !it.needVerification)) {
                    if (this@IntroFragment
                            .findNavController()
                            .currentDestination?.id ==
                        R.id.introFragment
                    ) {
                        this@IntroFragment.findNavController()
                            .navigate(
                                IntroFragmentDirections.actionIntroFragmentToLoginFragment())
                    }
                }

                if (it != null && it.needVerification) {
                    if (this@IntroFragment
                            .findNavController()
                            .currentDestination?.id ==
                        R.id.introFragment
                    ) {
                        this@IntroFragment
                            .findNavController()
                            .navigate(
                                IntroFragmentDirections
                                    .actionIntroFragmentToKeyVerification())
                    }
                }
                if (it != null && it.wasLoggedIn) {
                    if (this@IntroFragment
                            .findNavController()
                            .currentDestination?.id ==
                        R.id.introFragment
                    ) {
                        this@IntroFragment.findNavController()
                            .navigate(
                                IntroFragmentDirections
                                    .actionIntroFragmentToPregnantFragment()
                            )
                    }
                }
            })
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}


//if (this@IntroFragment
//                            .findNavController()
//                            .currentDestination?.id ==
//                        R.id.introFragment
//                    ){
//                this@IntroFragment.findNavController()
//                    .navigate(
//                        IntroFragmentDirections
//                            .actionIntroFragmentToPregnantFragment())
//            }