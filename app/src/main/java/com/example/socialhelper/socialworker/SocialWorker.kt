package com.example.socialhelper.socialworker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentSocialWorkerBinding

class SocialWorker : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentSocialWorkerBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_social_worker, container, false)
        val viewModel = ViewModelProvider(this)
            .get(SocialViewModel::class.java)
        binding.viewModel = viewModel

        // Inflate the layout for this fragment
        binding.lifecycleOwner = this
        return binding.root
    }
}