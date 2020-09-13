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
//                viewModel.allInfo.observe(viewLifecycleOwner, {info ->
//                    Log.e("id", info.serverID.toString())
                    lifecycleScope.launch {
                        if (adapter != null && adapter.isEnabled) {
                            if (socket == null) {
                                binding.getSpotButton.isEnabled = false
                                binding.getSpotButton.text = getString(R.string.wait)
                                viewModel.createConnection()
                                socket = viewModel.bluetoothReadWrite.btSocket
                            }
                            Log.e("adapter", "Adapter is available")
                            if (socket != null){
                                var i = 0
                                while (!socket.isConnected && i != 3){
                                    viewModel.bluetoothReadWrite.closeConnection()
                                    viewModel.createConnection()
                                    delay(5000)
                                    Log.e("trying", "trying to connect")
                                    i++
                                }
                                if (socket.isConnected) {
                                    Log.e("socket", "Socket is available")
                                    viewModel.sendMessage("1")
                                        binding.getSpotButton.isEnabled = true
                                        binding.getSpotButton.text = getString(R.string.get_spot)
                                        Log.e("sent", "1")
                                        binding.result.text = getString(R.string.spot_is_empty)
                                        viewModel.onDoneSetSpotFree()
                                } else {
                                    binding.getSpotButton.isEnabled = true
                                    binding.getSpotButton.text = getString(R.string.get_spot)
                                    Snackbar.make(
                                        binding.getSpotButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.onDoneSetSpotFree()
                                }
                            }
                        } else if (adapter != null && !adapter.isEnabled) {
                            Snackbar.make(
                                binding.getSpotButton,
                                getString(R.string.connect_to_bluetooth),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            delay(1500)
                            viewModel.bluetoothReadWrite.closeConnection()
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
//                })
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }
}