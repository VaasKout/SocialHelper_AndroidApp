package com.example.socialhelper.wheelchair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentWheelChairBinding

class WheelChair : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentWheelChairBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_wheel_chair, container, false)
        val viewModel =
            ViewModelProvider(this).get(WheelChairViewModel::class.java)
        binding.viewModel = viewModel



        binding.lifecycleOwner = this
        return binding.root
    }
}