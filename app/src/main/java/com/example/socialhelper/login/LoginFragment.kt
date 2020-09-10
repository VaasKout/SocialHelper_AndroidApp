package com.example.socialhelper.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialhelper.R
import com.example.socialhelper.database.Info
import com.example.socialhelper.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentLoginBinding.inflate(inflater)
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = loginViewModel

        loginViewModel.navigateToMainFrag.observe(viewLifecycleOwner, {
            if (it == true) {
                val userInput = binding.userInputEditText.text.toString()
                val passwordInput = binding.passwordInputEditText.text.toString()

                binding.userTextInput.error = null
                binding.passwordTextInput.error = null

                if (userInput.isEmpty()) {
                    lifecycleScope.launch {
                        binding.userTextInput.error = getString(R.string.user_input_error)
                        loginViewModel.onDoneNavigationToMain()
                        delay(3000)
                        binding.userTextInput.error = null
                    }

                }
                if (passwordInput.isEmpty()) {
                    lifecycleScope.launch {
                        binding.passwordTextInput.error = getString(R.string.password_input_error)
                        loginViewModel.onDoneNavigationToMain()
                        delay(3000)
                        binding.passwordTextInput.error = null
                    }
                }

                if (userInput.isNotEmpty() &&
                    passwordInput.isNotEmpty()
                ) {

                    loginViewModel.login = userInput
                    loginViewModel.password = passwordInput.toInt()
                    loginViewModel.allInfo.observe(viewLifecycleOwner, { info ->

                        lifecycleScope.launch {
                            if (info == null) {
                                loginViewModel.connectToServer()
                                loginViewModel.requestServer()
                                if (!loginViewModel.readWrite.socket.isConnected) {
                                    Snackbar.make(
                                        binding.loginNextButton,
                                        getString(R.string.retry_later),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    loginViewModel.onDoneNavigationToMain()

                                } else if (loginViewModel.serverID <= 0 ||
                                    loginViewModel.serverKey <= 0
                                ) {
                                    lifecycleScope.launch {
                                        binding.userTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        binding.passwordTextInput.error =
                                            getString(R.string.wrong_login_or_password)
                                        binding.forgotPassword.visibility = View.VISIBLE
                                        loginViewModel.onDoneNavigationToMain()
                                        delay(3000)
                                        binding.userTextInput.error = null
                                        binding.passwordTextInput.error = null
                                    }
                                }
                            }
                            if (info != null) {
                                if (info.serverID <= 0 && info.serverKey <= 0) {
                                    binding.userTextInput.error =
                                        getString(R.string.registration_denied)
                                    binding.userTextInput.error =
                                        getString(R.string.registration_denied)
                                    loginViewModel.onDoneNavigationToMain()

                                } else {
                                    when {
                                        info.login != userInput -> {
                                            binding.userTextInput.error =
                                                getString(R.string.wrong_login)
                                            loginViewModel.onDoneNavigationToMain()
                                        }
                                        info.password != passwordInput -> {
                                            binding.passwordTextInput.error =
                                                getString(R.string.wrong_password)
                                            loginViewModel.onDoneNavigationToMain()
                                            binding.forgotPassword.visibility = View.VISIBLE
                                        }
                                        else -> {
                                            Log.e("pregnant", "loging")
                                            val infoInstance = Info(
                                                id = 1,
                                                name = info.name,
                                                surname = info.surname,
                                                login = info.login,
                                                password = info.password,
                                                email = info.email,
                                                serverID = info.serverID,
                                                serverKey = info.serverKey,
                                                wasLoggedIn = true
                                            )
                                            loginViewModel.updateInfo(infoInstance)

                                            if (this@LoginFragment
                                                    .findNavController()
                                                    .currentDestination?.id ==
                                                R.id.loginFragment
                                            ) {
                                                this@LoginFragment.findNavController()
                                                    .navigate(
                                                        LoginFragmentDirections
                                                            .actionLoginFragmentToPregnantFragment()
                                                    )
                                                loginViewModel.onDoneNavigationToMain()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        })

                    loginViewModel.navigateToSignInFrag.observe(viewLifecycleOwner, {
                        if (it == true) {
                            loginViewModel.allInfo.let {
                                loginViewModel.onClear()
                            }
                            if (this@LoginFragment
                                    .findNavController()
                                    .currentDestination?.id ==
                                R.id.loginFragment) {
                                this@LoginFragment.findNavController()
                                    .navigate(LoginFragmentDirections
                                        .actionLoginFragmentToRegistrationFragment())
                                loginViewModel.onDoneNavigationToSign()
                            }
                        }
                    })

                    loginViewModel.navigateToRestoreFrag.observe(viewLifecycleOwner, {
                        if (it == true) {
                            this.findNavController()
                                .navigate(LoginFragmentDirections.actionLoginFragmentToRestorePassword())
                            loginViewModel.onDoneNavigationToRestore()
                        }
                    })

                    binding.lifecycleOwner = this
                    return binding.root
                }
        }