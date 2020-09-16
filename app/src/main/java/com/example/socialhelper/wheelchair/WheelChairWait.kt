package com.example.socialhelper.wheelchair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentWheelChairWaitBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
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

//        fun connect(){
//            lifecycleScope.launch {
//                if (viewModel.readWrite.socket == null){
//                    viewModel.connectToServer()
//                }
//                if (!viewModel.readWrite.socket.isConnected){
//                    Snackbar.make(
//                        binding.recyclerView,
//                        getString(R.string.retry_later),
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                    viewModel.triedToConnect = true
//                } else {
//                    if (viewModel.triedToConnect){
//                        Snackbar.make(
//                            binding.recyclerView,
//                            getString(R.string.connected),
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//                    }
//                    while (viewModel.readWrite.socket.isConnected){
//                        viewModel.readData()
//                        delay(1000)
//                    }
//                }
//            }
//        }
//        if (!viewModel.madeFirstConnection){
//            connect()
//            viewModel.madeFirstConnection = true
//        }

                lifecycleScope.launch {
                    whenResumed {

                    }
                }

        binding.lifecycleOwner = this
        return binding.root
    }
}