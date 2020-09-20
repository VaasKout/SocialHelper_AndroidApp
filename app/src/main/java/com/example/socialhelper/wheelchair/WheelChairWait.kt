package com.example.socialhelper.wheelchair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.databinding.FragmentWheelChairWaitBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


        fun connect(){
            lifecycleScope.launch {
                if (viewModel.readWrite.socket == null){
                    viewModel.connectToServer()
                }
                if (!viewModel.readWrite.socket.isConnected){
                    Snackbar.make(
                        binding.textWait,
                        getString(R.string.retry_later),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.triedToConnect = true
                } else {
                    if (viewModel.triedToConnect){
                        Snackbar.make(
                            binding.textWait,
                            getString(R.string.connected),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    while (viewModel.readWrite.socket.isConnected){
                        viewModel.requestServer()
                        delay(1000)
                        when(viewModel.state){
                            "accepted" -> {
                                binding.textWait.text = getString(R.string.order_accepted)
                            }
                            "complete" -> {
                                binding.textWait.text = getString(R.string.request_complete)
                                binding.textWait.setTextColor(
                                    ContextCompat
                                    .getColor(requireContext(), R.color.colorPrimary))
                                delay(3000)
                                this@WheelChairWait
                                    .findNavController()
                                    .navigate(WheelChairWaitDirections
                                        .actionWheelChairWaitToWheelChair())
                                viewModel.clear()
                            } else -> continue
                        }
                    }
                }
            }
        }

        binding.toolbarWheelchairWait.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.map ->{
                    this.findNavController()
                        .navigate(WheelChairWaitDirections
                            .actionWheelChairWaitToDialogMap())
                    true
                }
                R.id.refresh -> {
                    connect()
                    true
                }
                R.id.cancel -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Отменить заказ?")
                            .setNegativeButton("Нет") { _, _ ->
                            }
                            .setPositiveButton("Да") { _, _ ->
                                lifecycleScope.launch {
                                    if (viewModel.readWrite.socket.isConnected){
                                        viewModel.cancelOrder()
                                        Snackbar.make(
                                            binding.textWait,
                                            getString(R.string.order_cancel),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        delay(1000)
                                        this@WheelChairWait
                                            .findNavController()
                                            .navigate(WheelChairWaitDirections
                                                .actionWheelChairWaitToWheelChair())
                                        viewModel.clear()
                                    } else {
                                        Snackbar.make(
                                            binding.textWait,
                                            getString(R.string.retry_later),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        viewModel.triedToConnect = true
                                    }
                                }
                            }.show()
                    true
                }
                else -> false
            }
        }

        if (!viewModel.madeFirstConnection){
            connect()
            viewModel.madeFirstConnection = true
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}