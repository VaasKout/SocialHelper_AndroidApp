package com.example.socialhelper.pregnant


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentPregnantBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class PregnantFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentPregnantBinding=
            DataBindingUtil.inflate(inflater, R.layout.fragment_pregnant, container, false)
        val viewModel = ViewModelProvider(this).get(PregnantViewModel::class.java)
        binding.viewModel = viewModel

        lifecycleScope.launch {
            whenStarted {
            viewModel.turnOnBluetooth(requireActivity())
            }
        }

    viewModel.spotIsFree.observe(viewLifecycleOwner, {
        val adapter = viewModel.bluetoothReadWrite.btAdapter
        val socket = viewModel.bluetoothReadWrite.btSocket
            if (it == true) {
                lifecycleScope.launch {
                    if (adapter != null && adapter.isEnabled){
                        viewModel.createConnection()
                        if (socket != null){
                            viewModel.startBluetoothTransaction(1)
                            binding.result.text = viewModel.bluetoothAnswer.toString()
                        }
                    }
                    if (adapter != null && !adapter.isEnabled){
                        viewModel.turnOnBluetooth(requireActivity())

                    } else{
                        Snackbar.make(binding.getSpotButton,
                            getString(R.string.bluetooth_unavailable),
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
                viewModel.onDoneSetSpotFree()
            }
        })


        viewModel.exit.observe(viewLifecycleOwner, {
            if (it == true){
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Выйти из приложения?")
                    .setNegativeButton("Нет") { _, _ ->
                        viewModel.onDoneExit()
                    }
                    .setPositiveButton("Да") { _, _ ->
                        this.findNavController()
                            .navigate(PregnantFragmentDirections
                                .actionPregnantFragmentToLoginFragment())
                        viewModel.onDoneExit()
                    }.show()
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }

}