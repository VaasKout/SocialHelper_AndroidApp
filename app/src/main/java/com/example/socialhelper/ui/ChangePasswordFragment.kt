package com.example.socialhelper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentChangePasswordBinding
import com.example.socialhelper.viewmodels.ChangeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {


    /**
     * @see R.layout.fragment_change_password
     * Client enters it current password, new password and confirm it,
     * if fields is not empty and password is correct, according to Info database
     * new password writes to the server and inserts in database
     **/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChangePasswordBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_change_password, container, false)
        val viewModel =
            ViewModelProvider(this).get(ChangeViewModel::class.java)
        binding.viewModel = viewModel

        binding.oldPasswordEdit.addTextChangedListener {
            binding.oldPassword.error = null
        }

        binding.enterNewPasswordEdit.addTextChangedListener {
            binding.oldPassword.error = null
        }

        binding.confirmNewPassEdit.addTextChangedListener {
            binding.enterNewPassword.error = null
        }

        viewModel.changed.observe(viewLifecycleOwner, {
            viewModel.allInfo.observe(viewLifecycleOwner, { info ->

                if (it == true) {
                    val oldPass = binding.oldPasswordEdit.text.toString()
                    val newPass = binding.enterNewPasswordEdit.text.toString()
                    val confirmNewPass = binding.confirmNewPassEdit.text.toString()

                    if (oldPass.isEmpty()) {
                        binding.oldPassword.error = getString(R.string.empty_field_error)
                        viewModel.onDoneChange()
                    }
                    if (oldPass != info.password) {
                        binding.oldPassword.error = getString(R.string.wrong_password)
                        viewModel.onDoneChange()
                    }
                    if (newPass.isEmpty()) {
                        binding.enterNewPassword.error = getString(R.string.empty_field_error)
                        viewModel.onDoneChange()
                    }
                    if (confirmNewPass.isEmpty()) {
                        lifecycleScope.launch {
                            binding.confirmNewPassword.error = getString(R.string.empty_field_error)
                            viewModel.onDoneChange()
                            delay(3000)
                            binding.confirmNewPassword.error = null
                        }
                    }
                    if (newPass != confirmNewPass) {
                        binding.confirmNewPassword.error = getString(R.string.password_mismatch)
                        viewModel.onDoneChange()
                    }
                    if (oldPass.isNotEmpty() &&
                        oldPass == info.password &&
                        newPass.isNotEmpty() &&
                        confirmNewPass.isNotEmpty() &&
                        newPass == confirmNewPass
                    ) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Сменить пароль?")
                            .setNegativeButton("Нет") { _, _ ->
                            }
                            .setPositiveButton("Да") { _, _ ->
                                viewModel.allInfo.observe(viewLifecycleOwner, { info ->
                                    lifecycleScope.launch {
                                        viewModel.sendNewPass = newPass.toInt()
                                        viewModel.connectToServer()
                                        viewModel.requestServer()
                                        if (!viewModel.readWrite.socket.isConnected) {
                                            Snackbar.make(
                                                binding.buttonChangePass,
                                                getString(R.string.retry_later),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                            viewModel.onDoneChange()
                                        } else {
                                            val infoReference = Info(
                                                id = info.id,
                                                name = info.name,
                                                surname = info.surname,
                                                login = info.login,
                                                password = viewModel.receiveNewPass.toString(),
                                                email = info.email,
                                                reference = info.reference,
                                                serverID = info.serverID,
                                                serverKey = info.serverKey,
                                                category = info.category
                                            )
                                            viewModel.updateInfo(infoReference)
                                            //navigate back to main
                                            this@ChangePasswordFragment
                                                .findNavController()
                                                .popBackStack()
                                        }
                                    }
                                })
                            }.show()
                    }
                }
            })
        })

        /**
         * navigate back
         * @see FragmentChangePasswordBinding.exitFromChangePassword
         */

        binding.exitFromChangePassword.setOnClickListener {
            this.findNavController().popBackStack()
        }


        binding.lifecycleOwner = this
        return binding.root
    }
}