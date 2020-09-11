package com.example.socialhelper.registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentRegistrationBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_registration, container, false)

        val viewModel =
            ViewModelProvider(this).get(RegistrationViewModel::class.java)
        binding.viewModel = viewModel

        binding.toolbarReg.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.toolbarReg.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.clear) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Очистить поля ввода?")
                    .setNegativeButton("Нет") { _, _ ->
                    }
                    .setPositiveButton("Да") { _, _ ->
                        binding.textInputUserEditReg.setText("")
                        binding.textInputPassEditReg.setText("")
                        binding.confirmPasswordEdit.setText("")
                        binding.numberReferenceInputEdit.setText("")
                        binding.surnameEditInput.setText("")
                        binding.loginEdit.setText("")
                        binding.emailEdit.setText("")
                        viewModel.onClear()
                    }.show()
                true
            } else false
        }

        viewModel.navigateToWait.observe(viewLifecycleOwner, {

            if (it == true) {
                val userName = binding.textInputUserEditReg.text.toString()
                val password = binding.textInputPassEditReg.text.toString()
                val passwordConfirm = binding.confirmPasswordEdit.text.toString()
                val numberEdit = binding.numberReferenceInputEdit.text.toString()
                val surname = binding.surnameEditInput.text.toString()
                val login = binding.loginEdit.text.toString()
                val email = binding.emailEdit.text.toString()



                if (userName.isEmpty()) {
                    lifecycleScope.launch {
                        binding.userInputReg.error = getString(R.string.user_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.userInputReg.error = null
                    }
                }

                if (password.isEmpty()) {
                    lifecycleScope.launch {
                        binding.passwordInputReg.error = getString(R.string.password_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.passwordInputReg.error = null
                    }
                }

                if (login.isEmpty()) {
                    lifecycleScope.launch {
                        binding.login.error = getString(R.string.enter_login)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.login.error = null
                    }
                }

                if (surname.isEmpty()) {
                    lifecycleScope.launch {
                        binding.surnameInputReg.error = getString(R.string.surname_input_error)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.surnameInputReg.error = null
                    }
                }

                if (passwordConfirm.isEmpty()) {
                    lifecycleScope.launch {
                        binding.confirmPassword.error = getString(R.string.confirm_password)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.confirmPassword.error = null
                    }
                }

                if (email.isEmpty()) {
                    lifecycleScope.launch {
                        binding.email.error = "Введите почту"
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.email.error = null
                    }
                } else if (email.isNotEmpty() &&
                    (!email.contains("@") || !email.contains("."))
                ) {
                    lifecycleScope.launch {
                        binding.email.error = getString(R.string.wrong_email_input)
                        delay(3000)
                        binding.email.error = null
                    }
                }


                if (numberEdit.isEmpty()) {
                    lifecycleScope.launch {
                        binding.numberReference.error = getString(R.string.enter_reference_number)
                        viewModel.onDoneNavigating()
                        delay(3000)
                        binding.numberReference.error = null
                    }
                }

                if (passwordConfirm != password) {
                    binding.confirmPassword.error = getString(R.string.password_mismatch)
                    viewModel.onDoneNavigating()
                }

                if (userName.isNotEmpty() &&
                    (password.isNotEmpty() && password.length <= 8) &&
                    passwordConfirm.isNotEmpty() &&
                    (email.isNotEmpty() &&
                            email.contains("@") &&
                            email.contains(".")) &&
                    login.isNotEmpty() &&
                    surname.isNotEmpty() &&
                    numberEdit.isNotEmpty() &&
                    password == passwordConfirm) {

                    var info = Info(
                        id = 1, name = userName, surname = surname,
                        password = password, login = login,
                        email = email, wasLoggedIn = false)

                    if (binding.numberReference.isVisible) {
                        info = Info(
                            id = 1, name = userName, surname = surname,
                            password = password, reference = numberEdit.toInt(),
                            login = login, email = email, wasLoggedIn = false)

                        viewModel.referenceNumber = numberEdit.toInt()
                    }

                    viewModel.onInsert(info)

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Данные введены верно?")
                        .setNegativeButton("Нет") { _, _ ->
                            viewModel.onDoneNavigating()
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.allInfo.observe(viewLifecycleOwner, {info ->
                                lifecycleScope.launch {
                                    binding.regButton.isEnabled = false
                                    binding.regButton.text = getString(R.string.wait)
                                    viewModel.connectToServer()
                                    viewModel.requestServer()
                                    if (!viewModel.readWrite.socket.isConnected) {
                                        Snackbar.make(
                                            binding.regButton,
                                            getString(R.string.retry_later),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        binding.regButton.isEnabled = true
                                        binding.regButton.text = getString(R.string.register)
                                        viewModel.onDoneNavigating()
                                    } else {
                                        when (viewModel.state) {
                                            "wrong" -> {
                                                Log.e("state", viewModel.state)
                                                binding.login.error =
                                                    getString(R.string.try_another_login)
                                                binding.numberReference.error =
                                                    getString(R.string.check_reference)
                                                Snackbar.make(
                                                    binding.regButton,
                                                    getString(R.string.user_exist),
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                                binding.regButton.isEnabled = true
                                                binding.regButton.text = getString(R.string.register)
                                                viewModel.onDoneNavigating()
                                            } "wait" -> {
                                            Log.e("state", viewModel.state)
                                                val infoReference = Info(
                                                    id = info.id,
                                                    name = info.name,
                                                    surname = info.surname,
                                                    password = info.password,
                                                    login = info.login,
                                                    email = info.email,
                                                    reference = viewModel.referenceNumber,
                                                    needVerification = true
                                                )
                                                viewModel.updateInfo(infoReference)
                                                this@RegistrationFragment.findNavController()
                                                    .navigate(
                                                        RegistrationFragmentDirections
                                                            .actionRegistrationFragmentToKeyVerification())
                                                viewModel.onDoneNavigating()
                                            } else ->{
                                            Log.e("error",
                                                "unknown response: ${viewModel.state}")
                                            Snackbar.make(
                                                binding.regButton,
                                                getString(R.string.retry_later),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                            binding.regButton.isEnabled = true
                                            binding.regButton.text = getString(R.string.register)
                                            viewModel.onDoneNavigating()
                                            }
                                        }
                                    }
                                }
                            })
                        }.show()
                }
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}