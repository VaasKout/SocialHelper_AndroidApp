package com.example.socialhelper.verification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentKeyVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyVerification : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentKeyVerificationBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_key_verification, container, false)
        val viewModel = ViewModelProvider(this).get(KeyViewModel::class.java)
        binding.viewModel = viewModel

        if (!viewModel.notificationShowed) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Ключ верификации отправлен на указанную почту")
                .setMessage(
                    "Если письма нет в папке \"Входящие\", " +
                            "проверьте папку \"Спам\""
                )
                .setPositiveButton("Ок") { _, _ ->
                    viewModel.notificationShowed = true
                }.show()
        }

        viewModel.showNotification.observe(viewLifecycleOwner, {
            if (it == true) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Ключ верификации отправлен")
                    .setMessage(
                                "Зайдите на указанную почту, " +
                                "eсли письма нет в папке \"Входящие\", " +
                                "проверьте папку \"Спам\""
                    )
                    .setPositiveButton("Ок") { _, _ ->
                        viewModel.notificationShowed = true
                    }.show()
                viewModel.onDoneShow()
            }
        })


        viewModel.navigateBack.observe(viewLifecycleOwner, {
            if (it == true) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("При выходе все введенные данные будут утеряны")
                    .setMessage("Продолжить?")
                    .setNegativeButton("Нет") { _, _ ->
                        viewModel.onDoneNavigateBack()
                    }
                    .setPositiveButton("Да") { _, _ ->
                        this.findNavController()
                            .navigate(
                                KeyVerificationDirections
                                    .actionKeyVerificationToLoginFragment()
                            )
                        viewModel.onClear()
                        viewModel.onDoneNavigateBack()
                    }.show()
            }
        })

        viewModel.sendKey.observe(viewLifecycleOwner, {
            if (it == true) {
                val key = binding.keyEdit.text.toString()
                if (key.isEmpty()) {
                    lifecycleScope.launch {
                        binding.key.error = getString(R.string.empty_field_error)
                        viewModel.onDoneSendKey()
                        delay(3000)
                        binding.key.error = null
                    }
                }
                if (key.isNotEmpty()) {
                    viewModel.serverKey = key.toInt()

                    viewModel.allInfo.observe(viewLifecycleOwner, { info ->
                    lifecycleScope.launch {
                        binding.enterKeyButton.isEnabled = false
                        binding.enterKeyButton.text = getString(R.string.wait)
                        viewModel.connectToServer()
                        viewModel.requestServer()
                        if (!viewModel.readWrite.socket.isConnected) {
                            Snackbar.make(
                                binding.enterKeyButton,
                                getString(R.string.retry_later),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.enterKeyButton.isEnabled = true
                            binding.enterKeyButton.text = getString(R.string.verify)
                            viewModel.onDoneSendKey()
                        } else {
                            if (viewModel.serverId == 0) {
                                binding.key.error = getString(R.string.wrong_key)
                                viewModel.onDoneSendKey()
                                binding.enterKeyButton.isEnabled = true
                                binding.enterKeyButton.text = getString(R.string.verify)
                                delay(3000)
                                binding.key.error = null
                            } else if (viewModel.serverId > 0) {
                                    binding.key.error = null
                                    val infoInstance = Info(
                                        id = 1,
                                        name = info.name,
                                        surname = info.surname,
                                        login = info.login,
                                        password = info.password,
                                        email = info.email,
                                        serverID = viewModel.serverId,
                                        serverKey = key.toInt(),
                                        needVerification = false
                                    )
                                    viewModel.updateInfo(infoInstance)

                                this@KeyVerification.findNavController()
                                    .navigate(
                                        KeyVerificationDirections
                                            .actionKeyVerificationToResponseFragment()
                                    )
                                    viewModel.onDoneSendKey()
                                }
                            }
                        }
                    })
                }
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }
}