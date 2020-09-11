package com.example.socialhelper.pregnant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentPregnantBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PregnantFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentPregnantBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant, container, false)
        val viewModel = ViewModelProvider(this).get(PregnantViewModel::class.java)
        binding.viewModel = viewModel

        binding.toolbarPregnant.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.change_pass -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Сменить пароль?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            this.findNavController()
                                .navigate(PregnantFragmentDirections
                                    .actionPregnantFragmentToChangePassword())
                        }.show()
                    true
                }
                R.id.exit_from_pregnant ->{
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Выйти из аккаунта?")
                        .setNegativeButton("Нет") { _, _ ->
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.onClear()
                            this.findNavController()
                                .navigate(
                                    PregnantFragmentDirections
                                        .actionPregnantFragmentToLoginFragment())
                        }.show()
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launchWhenStarted {
                viewModel.turnOnBluetooth(requireActivity())
        }

        viewModel.spotIsFree.observe(viewLifecycleOwner, {
            var adapter = viewModel.bluetoothReadWrite.btAdapter
            var socket = viewModel.bluetoothReadWrite.btSocket
            if (it == true) {
                viewModel.allInfo.observe(viewLifecycleOwner, {info ->
                    Log.e("id", info.serverID.toString())
                    lifecycleScope.launch {
                        if (adapter != null && adapter.isEnabled) {
                            if (socket == null || !socket.isConnected) {
                                viewModel.createConnection()
                                socket = viewModel.bluetoothReadWrite.btSocket
                            }
                            Log.e("adapter", "Adapter is available")
                            if (socket != null){
                                while (!socket.isConnected){
                                    viewModel.createConnection()
                                    delay(5000)
                                    socket = viewModel.bluetoothReadWrite.btSocket
                                    Log.e("trying", "trying to connect")
                                }
                                if (socket.isConnected) {
                                    Log.e("socket", "Socket is available")
                                    viewModel.sendMessage(info.serverID.toString())
                                    Log.e("sent", info.serverID.toString())
//                            binding.result.text = viewModel.bluetoothAnswer.toString()
                                    viewModel.onDoneSetSpotFree()
                                }
                            }
                        } else if (adapter != null && !adapter.isEnabled) {
                            viewModel.turnOnBluetooth(requireActivity())
                            adapter = viewModel.bluetoothReadWrite.btAdapter
                            viewModel.onDoneSetSpotFree()

                        } else {
                            Snackbar.make(
                                binding.getSpotButton,
                                getString(R.string.bluetooth_unavailable),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            viewModel.onDoneSetSpotFree()
                        }
                    }
                })
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }
}