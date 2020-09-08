package com.example.socialhelper.response


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentResponceBinding

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

        viewModel.userInfo.observe(viewLifecycleOwner, {
            if (it.serverID <= 0) {
                binding.respondText.text = getString(R.string.registration_denied)
            } else {
                binding.respondText.text = getString(R.string.positive_response)
            }
        })


//        lifecycleScope.launch {
//            whenResumed {
//                    while (viewModel.serverKey <= 0){
//                        viewModel.connectToServer()
//                        if (!viewModel.readWrite.socket.isConnected){
//                            Toast.makeText(requireContext(),
//                                getString(R.string.connection_interrupted),
//                                Toast.LENGTH_SHORT).show()
//                            break
//                        } else {
//                        viewModel.sendID()
//                        viewModel.readServerKey()
//                        binding.serverKey.text = viewModel.serverKey.toString()
//                        delay(3000)
//                        }
//                    }
//                        if (viewModel.serverKey >= 0){
//                    binding.serverKey.text = viewModel.serverKey.toString()
//                    viewModel.userInfo.observe(viewLifecycleOwner, {
//                        if (viewModel.serverKey != 0){
//                            val info = Info(
//                                it.id,
//                                it.name,
//                                it.password,
//                                it.group,
//                                it.serverID,
//                                viewModel.serverKey)
//                            viewModel.onUpdate(info)
//                        }
//                    })
//                 }
//            }
//        }

        binding.lifecycleOwner = this
        return binding.root
    }
}