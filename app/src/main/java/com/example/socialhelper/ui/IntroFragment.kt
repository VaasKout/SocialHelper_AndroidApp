package com.example.socialhelper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentIntroBinding
import com.example.socialhelper.viewmodels.IntroViewModel
import kotlinx.coroutines.launch

class IntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /**
         * Intro fragment with text appearance animation
         * Navigation from here depends on tables
         *
         *  if didn't registered navigate to @see LoginFragment
         *  if key didn't verified navigate to @see KeyVerification
         *  if key verified and registered navigate to one of three
         *      clients depends on category @see Pregnant, WheelChair, SocialWorker
         *  if WheelChair made an order navigate to @see WheelChairWait
         *
         * @see R.layout.fragment_intro
         *
         */

        val binding: FragmentIntroBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)

        val viewModel = ViewModelProvider(this)
            .get(IntroViewModel::class.java)

        val wordList =
            listOf(binding.welcome1, binding.welcome2, binding.welcome3, binding.welcome4)
        val categoryList = resources.getStringArray(R.array.category)
        val categoryEng = resources.getStringArray(R.array.categoryEng)

//            if (this@IntroFragment
//                        .findNavController()
//                        .currentDestination?.id ==
//                    R.id.introFragment){
//            this@IntroFragment.findNavController()
//                                    .navigate(
//                                        IntroFragmentDirections
//                                            .actionIntroFragmentToSocialWorker())
//            }

        lifecycleScope.launch {
            viewModel.animate(wordList)
            viewModel.allInfo.observe(viewLifecycleOwner, {
                if (this@IntroFragment
                        .findNavController()
                        .currentDestination?.id ==
                    R.id.introFragment
                ) {
                    if (it == null) {

                        this@IntroFragment.findNavController()
                            .navigate(
                                IntroFragmentDirections.actionIntroFragmentToLoginFragment()
                            )
                    }

                    if (it != null && it.needVerification) {
                        if (this@IntroFragment
                                .findNavController()
                                .currentDestination?.id ==
                            R.id.introFragment
                        ) {
                            this@IntroFragment.findNavController().navigate(
                                IntroFragmentDirections.actionIntroFragmentToKeyVerification()
                            )
                        }
                    }
                    if (it != null && !it.needVerification) {
                        when (it.category) {
                            categoryList[0], categoryEng[0] -> {
                                viewModel.data.observe(viewLifecycleOwner, { data ->
                                    if (data == null) {
                                        this@IntroFragment.findNavController()
                                            .navigate(
                                                IntroFragmentDirections.actionIntroFragmentToWheelChair()
                                            )
                                    } else {
                                        this@IntroFragment.findNavController()
                                            .navigate(
                                                IntroFragmentDirections.actionIntroFragmentToWheelChairWait()
                                            )
                                    }
                                })
                            }
                            categoryList[1], categoryEng[1] -> {
                                this@IntroFragment.findNavController()
                                    .navigate(
                                        IntroFragmentDirections.actionIntroFragmentToPregnantFragment()
                                    )
                            }
                            categoryList[2], categoryEng[2] -> {
                                this@IntroFragment.findNavController()
                                    .navigate(
                                        IntroFragmentDirections.actionIntroFragmentToSocialWorker()
                                    )
                            }
                        }
                    }
                }

            })
        }


        binding.lifecycleOwner = this
        return binding.root
    }
}


