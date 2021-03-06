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
import com.example.socialhelper.databinding.FragmentRestorePasswordBinding
import com.example.socialhelper.viewmodels.RestoreViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RestorePasswordFragment : Fragment() {

    /**
     * @see R.layout.fragment_restore_password
     * One of the clients enters it's login and email and
     * if fields is not empty and correct according to Info database,
     * client gets it password back on email
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentRestorePasswordBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_restore_password, container, false)
        val viewModel = ViewModelProvider(this).get(RestoreViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateBackToLogin.observe(viewLifecycleOwner, {
            if (it == true) {
                this.findNavController().popBackStack()
                viewModel.onDoneNavigateBack()
            }
        })
        /**
         * textChangeListeners remove errors from EditTextFields
         */
        binding.loginRestoreEdit.addTextChangedListener {
            binding.loginRestore.error = null
        }
        binding.restoreEmailEdit.addTextChangedListener {
            binding.emailRestore.error = null
        }
        /**
         * @see FragmentRestorePasswordBinding.sendRestoreRequestButton
         */
        viewModel.restoreNavigateToLogin.observe(viewLifecycleOwner, {
            if (it == true) {

                val login = binding.loginRestoreEdit.text.toString()
                val email = binding.restoreEmailEdit.text.toString()

                if (login.isEmpty()) {
                    binding.loginRestore.error = getString(R.string.empty_field_error)
                    viewModel.onDoneRestore()
                }

                if (email.isEmpty()) {
                    binding.emailRestore.error = getString(R.string.empty_field_error)
                    viewModel.onDoneRestore()
                }

                if (login.isNotEmpty() &&
                    email.isNotEmpty()
                ) {
                    viewModel.allInfo.observe(viewLifecycleOwner, { info ->
                        if (info != null) {
                            if (info.login != login) {
                                binding.loginRestore.error = getString(R.string.wrong_login)
                                viewModel.onDoneRestore()
                            }
                            if (info.email != email) {
                                binding.emailRestore.error = getString(R.string.wrong_email)
                                viewModel.onDoneRestore()
                            }
                        } else {
                            lifecycleScope.launch {
                                viewModel.loginRestore = login
                                viewModel.emailRestore = email
                                viewModel.connectToServer()
                                viewModel.requestServer()
                                if (!viewModel.readWrite.socket.isConnected) {
                                    Snackbar.make(
                                        binding.sendRestoreRequestButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.onDoneRestore()
                                } else {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Пароль отправлен на почту")
                                        .setMessage(
                                            "Если письма нет в папке \"Входящие\", " +
                                                    "проверьте папку \"Спам\""
                                        )
                                        .setPositiveButton("Ок") { _, _ ->
                                            this@RestorePasswordFragment.findNavController()
                                                .popBackStack()
                                            viewModel.onDoneRestore()
                                        }.show()
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