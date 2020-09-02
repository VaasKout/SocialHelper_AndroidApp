package com.example.socialhelper.response


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentResponceBinding
import kotlinx.coroutines.*

class ResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentResponceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_responce, container, false)
        val viewModel =
            ViewModelProvider(this).get(ResponseViewModel::class.java)
        binding.viewModel = viewModel

        lifecycleScope.launch {

            whenStarted {
                if (viewModel.serverKey == 0) {
                    viewModel.connectToServer()
                    viewModel.getServerKey()
                }
            }

            whenResumed {
                if (!viewModel.readWrite.socket.isConnected){
                    Toast.makeText(requireContext(),
                        getString(R.string.connection_interrupted),
                        Toast.LENGTH_SHORT).show()
                } else{
                    while (viewModel.serverKey <= 0){
                        viewModel.readKey()
                        binding.serverKey.text = viewModel.serverKey.toString()
                        delay(3000)
                    }
                    binding.serverKey.text = viewModel.serverKey.toString()
                    viewModel.userInfo.observe(viewLifecycleOwner, {
                        if (viewModel.serverKey != 0){
                            val info = Info(
                                it.id,
                                it.name,
                                it.password,
                                it.group,
                                it.serverID,
                                viewModel.serverKey)
                            viewModel.onUpdate(info)
                        }
                    })
                }
            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}