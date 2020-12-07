package com.example.socialhelper.ui

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
import com.example.socialhelper.viewmodels.WaitViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaitFragment : Fragment() {

    /**
     * This fragment connects to the server and checks state of order
     * if order is complete or canceled navigates to WheelChair fragment
     *
     * @see WheelChairFragment
     * @see R.layout.fragment_wheel_chair_wait
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentWheelChairWaitBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_wheel_chair_wait, container, false)
        val viewModel = ViewModelProvider(this)
            .get(WaitViewModel::class.java)
        binding.viewModel = viewModel

        /**
         * Connects to the server when client makes order or presses update icon on toolbar
         */

        fun connect() {
            lifecycleScope.launch {
                viewModel.connectToServer()
                if (!viewModel.readWrite.socket.isConnected) {
                    Snackbar.make(
                        binding.textWait,
                        getString(R.string.retry_later),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.triedToConnect = true
                } else {
                    if (viewModel.triedToConnect) {
                        Snackbar.make(
                            binding.textWait,
                            getString(R.string.connected),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    while (viewModel.readWrite.socket.isConnected) {
                        viewModel.requestServer()
                        when (viewModel.state) {
                            "helpAccepted" -> {
                                binding.textWait.text = getString(R.string.order_accepted)
                            }
                            "complete" -> {
                                binding.textWait.text = getString(R.string.request_complete)
                                binding.textWait.setTextColor(
                                    ContextCompat
                                        .getColor(requireContext(), R.color.colorPrimary)
                                )
                                delay(3000)
                                this@WaitFragment
                                    .findNavController()
                                    .navigate(
                                        WaitFragmentDirections
                                            .actionWheelChairWaitToWheelChair()
                                    )
                                viewModel.clear()
                            }
                            else -> continue
                        }
                    }
                }
            }
        }

        /**
         * @see FragmentWheelChairWaitBinding.toolbarWheelchairWait
         * @see R.menu.top_bar_wait
         */
        binding.toolbarWheelchairWait.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.map -> {
                    this.findNavController()
                        .navigate(WaitFragmentDirections.actionWheelChairWaitToDialogMap())
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
                                if (viewModel.readWrite.socket.isConnected) {
                                    viewModel.cancelOrder()
                                    Snackbar.make(
                                        binding.textWait,
                                        getString(R.string.order_cancel),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    delay(1000)
                                    this@WaitFragment
                                        .findNavController()
                                        .navigate(WaitFragmentDirections.actionWheelChairWaitToWheelChair())
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
                            this@WaitFragment
                                .findNavController()
                                .navigate(WaitFragmentDirections.actionWheelChairWaitToWheelChair())
                        }.show()
                    true
                }
                else -> false
            }
        }

        if (!viewModel.madeFirstConnection) {
            connect()
            viewModel.madeFirstConnection = true
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}