package com.example.socialhelper.wheelchair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentWheelChairBinding
import com.example.socialhelper.databinding.FragmentWheelChairWaitBinding
import kotlinx.coroutines.launch

class WheelChairWait : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWheelChairWaitBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_wheel_chair_wait, container, false)
        val viewModel = ViewModelProvider(this)
            .get(WaitViewModel::class.java)
        binding.viewModel = viewModel

        lifecycleScope.launch {
//            while (viewModel.readWrite.socket.isConnected){
//                val s = viewModel.readWrite.readLine()
//                binding.textWait.text = s
//                break
//            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}